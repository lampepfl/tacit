package tacit.agents
package llm.endpoint

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.responses.{
  ResponseCreateParams, Response, ResponseInputItem, ResponseOutputItem,
  ResponseOutputMessage, ResponseOutputText, ResponseStreamEvent,
  EasyInputMessage, FunctionTool,
  ResponseFunctionToolCall, ResponseReasoningItem,
  ResponseStatus, ResponseUsage as OAIUsage,
}
import com.openai.models.{Reasoning, ReasoningEffort, ResponsesModel}
import com.openai.core.JsonValue
import scala.jdk.CollectionConverters.*
import gears.async.{Async, Future, BufferedChannel, ReadableChannel}
import tacit.agents.utils.Result

/** OpenAI endpoint using the Responses API (`/v1/responses`).
  *
  * This is the default and recommended OpenAI endpoint. It supports tools and
  * reasoning/thinking simultaneously, which the older Chat Completions API does not.
  *
  * For the legacy Chat Completions API, use [[OpenAICompletionEndpoint]].
  */
class OpenAIEndpoint(config: EndpointConfig) extends Endpoint:

  private lazy val client: OpenAIClient =
    OpenAIOkHttpClient.builder()
      .apiKey(config.apiKey)
      .baseUrl(config.baseUrl)
      .build()

  private def buildParams(messages: List[Message], llmConfig: LLMConfig): ResponseCreateParams.Builder =
    val builder = ResponseCreateParams.builder()
      .model(ResponsesModel.ofString(llmConfig.model))

    llmConfig.maxTokens.foreach(n => builder.maxOutputTokens(n.toLong))
    llmConfig.systemPrompt.foreach(p => builder.instructions(p))

    val inputItems = scala.collection.mutable.ListBuffer[ResponseInputItem]()

    messages.foreach: msg =>
      msg.role match
        case Role.System => () // handled by instructions
        case Role.User =>
          val toolResults = msg.content.collect { case tr: Content.ToolResult => tr }
          if toolResults.nonEmpty then
            toolResults.foreach: tr =>
              inputItems += ResponseInputItem.ofFunctionCallOutput(
                ResponseInputItem.FunctionCallOutput.builder()
                  .callId(tr.toolUseId)
                  .output(tr.content)
                  .build()
              )
          else
            inputItems += ResponseInputItem.ofEasyInputMessage(
              EasyInputMessage.builder()
                .role(EasyInputMessage.Role.USER)
                .content(msg.text)
                .build()
            )
        case Role.Assistant =>
          val text = msg.text
          if text.nonEmpty then
            inputItems += ResponseInputItem.ofEasyInputMessage(
              EasyInputMessage.builder()
                .role(EasyInputMessage.Role.ASSISTANT)
                .content(text)
                .build()
            )
          msg.content.collect { case tu: Content.ToolUse => tu }.foreach: tu =>
            inputItems += ResponseInputItem.ofFunctionCall(
              ResponseFunctionToolCall.builder()
                .callId(tu.id)
                .name(tu.name)
                .arguments(tu.input)
                .build()
            )

    builder.inputOfResponse(inputItems.toList.asJava)

    llmConfig.temperature.foreach(t => builder.temperature(t))
    llmConfig.topP.foreach(p => builder.topP(p))

    if llmConfig.tools.nonEmpty then
      llmConfig.tools.foreach: tool =>
        builder.addTool(FunctionTool.builder()
          .name(tool.name)
          .description(tool.description)
          .parameters(convertParameters(tool.parameters))
          .strict(false)
          .build())

    llmConfig.thinking.foreach:
      case ThinkingMode.Disabled => ()
      case ThinkingMode.Auto =>
        builder.reasoning(Reasoning.builder().effort(ReasoningEffort.MEDIUM).build())
      case ThinkingMode.Effort(level) =>
        val effort = level match
          case EffortLevel.Low => ReasoningEffort.LOW
          case EffortLevel.Medium => ReasoningEffort.MEDIUM
          case EffortLevel.High => ReasoningEffort.HIGH
          case EffortLevel.XHigh => ReasoningEffort.XHIGH
        builder.reasoning(Reasoning.builder().effort(effort).build())
      case ThinkingMode.Budget(n) =>
        throw IllegalArgumentException(s"Budget($n) is not valid for OpenAI. Use ThinkingMode.Effort instead.")

    builder

  override def invoke(messages: List[Message], llmConfig: LLMConfig): Result[ChatResponse, LLMError] =
    try
      val response = client.responses().create(buildParams(messages, llmConfig).build())
      Right(convertResponse(response))
    catch
      case e: Exception =>
        Left(LLMError(s"OpenAI API error: ${e.getMessage}"))

  override def stream(messages: List[Message], llmConfig: LLMConfig)(using Async.Spawn): ReadableChannel[Result[StreamEvent, LLMError]] =
    val ch = BufferedChannel[Result[StreamEvent, LLMError]](16)
    Future:
      try
        val streamResponse = client.responses().createStreaming(buildParams(messages, llmConfig).build())
        val iterator = streamResponse.stream().iterator().asScala

        // Accumulators
        val textBuf = new StringBuilder
        val thinkingBuf = new StringBuilder
        val toolCalls = scala.collection.mutable.Map[String, (Int, String, StringBuilder)]() // itemId -> (outputIndex, name, args)
        var lastResponse: Response | Null = null

        while iterator.hasNext do
          val event = iterator.next()

          if event.isOutputTextDelta then
            val delta = event.outputTextDelta().get().nn
            val text = delta.delta()
            textBuf.append(text)
            ch.send(Right(StreamEvent.Delta(text)))

          else if event.isReasoningTextDelta then
            val delta = event.reasoningTextDelta().get().nn
            val text = delta.delta()
            thinkingBuf.append(text)
            ch.send(Right(StreamEvent.ThinkingDelta(text)))

          else if event.isOutputItemAdded then
            val added = event.outputItemAdded().get().nn
            val item = added.item()
            val idx = added.outputIndex().toInt
            if item.isFunctionCall then
              val fc = item.functionCall().get().nn
              val callId = fc.callId()
              toolCalls(fc.callId()) = (idx, fc.name(), new StringBuilder)
              ch.send(Right(StreamEvent.ToolCallStart(idx, callId, fc.name())))

          else if event.isFunctionCallArgumentsDelta then
            val delta = event.functionCallArgumentsDelta().get().nn
            val itemId = delta.itemId()
            val argsDelta = delta.delta()
            toolCalls.get(itemId).foreach: (idx, _, buf) =>
              buf.append(argsDelta)
            ch.send(Right(StreamEvent.ToolCallDelta(delta.outputIndex().toInt, argsDelta)))

          else if event.isCompleted then
            lastResponse = event.completed().get().nn.response()

        streamResponse.close()

        // Build final ChatResponse from the completed response
        val response =
          if lastResponse != null then convertResponse(lastResponse)
          else
            // Fallback: build from accumulated state
            val contents = scala.collection.mutable.ListBuffer[Content]()
            if thinkingBuf.nonEmpty then
              contents += Content.Thinking(thinkingBuf.toString)
            if textBuf.nonEmpty then
              contents += Content.Text(textBuf.toString)
            toolCalls.toList.sortBy(_._2._1).foreach: (id, tuple) =>
              contents += Content.ToolUse(id, tuple._2, tuple._3.toString)
            val finishReason =
              if toolCalls.nonEmpty then FinishReason.ToolUse else FinishReason.Stop
            ChatResponse(
              message = Message(Role.Assistant, contents.toList),
              finishReason = finishReason,
              usage = None,
            )
        ch.send(Right(StreamEvent.Done(response)))
        ch.close()
      catch
        case e: Exception =>
          ch.send(Left(LLMError(s"OpenAI API error: ${e.getMessage}")))
          ch.close()
    ch.asReadable

  private def convertResponse(response: Response): ChatResponse =
    val contents = scala.collection.mutable.ListBuffer[Content]()
    val output = response.output().asScala

    output.foreach: item =>
      if item.isReasoning then
        val reasoning = item.reasoning().get().nn
        val summaryText = reasoning.summary().asScala.map(_.text()).mkString
        if summaryText.nonEmpty then
          contents += Content.Thinking(summaryText)

      else if item.isMessage then
        val msg = item.message().get().nn
        msg.content().asScala.foreach: contentItem =>
          if contentItem.isOutputText then
            val text = contentItem.asOutputText().text()
            if text.nonEmpty then
              contents += Content.Text(text)

      else if item.isFunctionCall then
        val fc = item.functionCall().get().nn
        contents += Content.ToolUse(
          id = fc.callId(),
          name = fc.name(),
          input = fc.arguments(),
        )

    val hasFunctionCalls = output.exists(_.isFunctionCall)
    val finishReason =
      if hasFunctionCalls then FinishReason.ToolUse
      else
        response.status().map[FinishReason](status =>
          if status == ResponseStatus.COMPLETED then FinishReason.Stop
          else if status == ResponseStatus.INCOMPLETE then FinishReason.MaxTokens
          else FinishReason.Other(status.asString())
        ).orElse(FinishReason.Stop)

    val usage = response.usage().map[Usage](u =>
      Usage(inputTokens = u.inputTokens(), outputTokens = u.outputTokens())
    )

    ChatResponse(
      message = Message(Role.Assistant, contents.toList),
      finishReason = finishReason,
      usage = if usage.isPresent then Some(usage.get().nn) else None,
    )

  private def convertParameters(params: ToolSchema.Parameters): FunctionTool.Parameters =
    val propsMap = new java.util.LinkedHashMap[String, Any]()
    params.properties.foreach: (name, prop) =>
      val propMap = new java.util.LinkedHashMap[String, Any]()
      propMap.put("type", prop.`type`)
      if prop.description.nonEmpty then
        propMap.put("description", prop.description)
      if prop.enumValues.nonEmpty then
        propMap.put("enum", java.util.List.of(prop.enumValues*))
      propsMap.put(name, propMap)

    FunctionTool.Parameters.builder()
      .putAdditionalProperty("type", JsonValue.from("object"))
      .putAdditionalProperty("properties", JsonValue.from(propsMap))
      .putAdditionalProperty("required", JsonValue.from(java.util.List.of(params.required*)))
      .build()

object OpenAIEndpoint extends EndpointProvider:
  type EndpointType = OpenAIEndpoint

  override def create(config: EndpointConfig): OpenAIEndpoint =
    OpenAIEndpoint(config)

  override def createFromEnv(): OpenAIEndpoint =
    val apiKey = sys.env.getOrElse("OPENAI_API_KEY",
      throw RuntimeException("OPENAI_API_KEY environment variable is not set"))
    val baseUrl = sys.env.getOrElse("OPENAI_BASE_URL", "https://api.openai.com/v1")
    create(EndpointConfig(baseUrl = baseUrl, apiKey = apiKey))
