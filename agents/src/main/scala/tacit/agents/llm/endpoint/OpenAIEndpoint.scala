package tacit.agents
package llm.endpoint

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletion
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionToolMessageParam
import com.openai.models.chat.completions.ChatCompletionChunk
import com.openai.models.chat.completions.ChatCompletionStreamOptions
import com.openai.models.FunctionDefinition
import com.openai.models.FunctionParameters
import com.openai.models.ReasoningEffort
import com.openai.core.JsonValue
import scala.jdk.CollectionConverters.*
import tacit.agents.utils.Result

class OpenAIEndpoint(config: EndpointConfig) extends Endpoint:

  private lazy val client: OpenAIClient =
    OpenAIOkHttpClient.builder()
      .apiKey(config.apiKey)
      .baseUrl(config.baseUrl)
      .build()

  private def buildParams(messages: List[Message], llmConfig: LLMConfig): ChatCompletionCreateParams.Builder =
    val builder = ChatCompletionCreateParams.builder()
      .model(llmConfig.model)

    llmConfig.systemPrompt.foreach(p => builder.addSystemMessage(p))

    messages.foreach: msg =>
      msg.role match
        case Role.System =>
          builder.addSystemMessage(msg.text)
        case Role.User =>
          msg.content match
            case List(Content.ToolResult(toolUseId, content, _)) =>
              builder.addMessage(
                ChatCompletionToolMessageParam.builder()
                  .toolCallId(toolUseId)
                  .content(content)
                  .build()
              )
            case _ =>
              builder.addUserMessage(msg.text)
        case Role.Assistant =>
          builder.addAssistantMessage(msg.text)

    llmConfig.temperature.foreach(t => builder.temperature(t))
    llmConfig.maxTokens.foreach(n => builder.maxCompletionTokens(n.toLong))
    llmConfig.topP.foreach(p => builder.topP(p))
    if llmConfig.stopSequences.nonEmpty then
      builder.stop(
        ChatCompletionCreateParams.Stop.ofStrings(
          java.util.List.of(llmConfig.stopSequences*)
        )
      )

    if llmConfig.tools.nonEmpty then
      llmConfig.tools.foreach: tool =>
        builder.addFunctionTool(
          FunctionDefinition.builder()
            .name(tool.name)
            .description(tool.description)
            .parameters(convertParameters(tool.parameters))
            .build()
        )

    llmConfig.thinking.foreach:
      case ThinkingMode.Disabled => builder.reasoningEffort(ReasoningEffort.NONE)
      case ThinkingMode.Auto => builder.reasoningEffort(ReasoningEffort.MEDIUM)
      case ThinkingMode.Effort(EffortLevel.Low) => builder.reasoningEffort(ReasoningEffort.LOW)
      case ThinkingMode.Effort(EffortLevel.Medium) => builder.reasoningEffort(ReasoningEffort.MEDIUM)
      case ThinkingMode.Effort(EffortLevel.High) => builder.reasoningEffort(ReasoningEffort.HIGH)
      case ThinkingMode.Effort(EffortLevel.XHigh) => builder.reasoningEffort(ReasoningEffort.XHIGH)
      case ThinkingMode.Budget(n) =>
        throw IllegalArgumentException(s"Budget($n) is not valid for OpenAI. Use ThinkingMode.Effort instead.")

    builder

  override def invoke(messages: List[Message], llmConfig: LLMConfig): Result[ChatResponse, LLMError] =
    try
      val completion = client.chat().completions().create(buildParams(messages, llmConfig).build())
      val choice = completion.choices().get(0)
      Right(convertResponse(choice, completion))
    catch
      case e: Exception =>
        Left(LLMError(s"OpenAI API error: ${e.getMessage}"))

  override def stream(messages: List[Message], llmConfig: LLMConfig): LazyList[Result[StreamEvent, LLMError]] =
    try
      val params = buildParams(messages, llmConfig)
        .streamOptions(ChatCompletionStreamOptions.builder().includeUsage(true).build())
        .build()
      val streamResponse = client.chat().completions().createStreaming(params)
      val iterator = streamResponse.stream().iterator().asScala

      // Accumulators for building the final ChatResponse
      val textBuf = new StringBuilder
      val toolCalls = scala.collection.mutable.Map[Int, (String, String, StringBuilder)]() // index -> (id, name, args)
      var lastFinishReason: FinishReason = FinishReason.Stop
      var lastUsage: Option[Usage] = None

      def convertChunk(chunk: ChatCompletionChunk): List[Result[StreamEvent, LLMError]] =
        val events = scala.collection.mutable.ListBuffer[Result[StreamEvent, LLMError]]()
        val choices = chunk.choices()
        if !choices.isEmpty then
          val choice = choices.get(0)
          val delta = choice.delta()

          // Text delta
          delta.content().ifPresent: text =>
            if text.nn.nonEmpty then
              textBuf.append(text.nn)
              events += Right(StreamEvent.Delta(text.nn))

          // Tool call deltas
          delta.toolCalls().ifPresent: tcs =>
            tcs.forEach: tc =>
              val idx = tc.index().toInt
              tc.id().ifPresent: id =>
                val name = tc.function().flatMap(f => f.name()).orElse("").nn
                toolCalls(idx) = (id.nn, name, new StringBuilder)
                events += Right(StreamEvent.ToolCallStart(idx, id.nn, name))
              tc.function().ifPresent: fn =>
                fn.arguments().ifPresent: args =>
                  toolCalls.get(idx).foreach: (_, _, buf) =>
                    buf.append(args.nn)
                  events += Right(StreamEvent.ToolCallDelta(idx, args.nn))

          // Finish reason
          if choice.finishReason().isPresent then
            lastFinishReason = choice.finishReason().get().nn.toString match
              case "stop"       => FinishReason.Stop
              case "length"     => FinishReason.MaxTokens
              case "tool_calls" => FinishReason.ToolUse
              case other        => FinishReason.Other(other)

        // Usage (appears in final chunk)
        chunk.usage().ifPresent: u =>
          lastUsage = Some(Usage(
            inputTokens = u.promptTokens(),
            outputTokens = u.completionTokens(),
          ))

        events.toList

      def buildLazyList(iter: Iterator[ChatCompletionChunk]): LazyList[Result[StreamEvent, LLMError]] =
        if iter.hasNext then
          val chunk = iter.next()
          val events = convertChunk(chunk)
          LazyList.from(events) #::: buildLazyList(iter)
        else
          streamResponse.close()
          // Build final ChatResponse
          val contents = scala.collection.mutable.ListBuffer[Content]()
          if textBuf.nonEmpty then
            contents += Content.Text(textBuf.toString)
          toolCalls.toList.sortBy(_._1).foreach: (_, tuple) =>
            contents += Content.ToolUse(tuple._1, tuple._2, tuple._3.toString)
          val response = ChatResponse(
            message = Message(Role.Assistant, contents.toList),
            finishReason = lastFinishReason,
            usage = lastUsage,
          )
          LazyList(Right(StreamEvent.Done(response)))

      buildLazyList(iterator)
    catch
      case e: Exception =>
        LazyList(Left(LLMError(s"OpenAI API error: ${e.getMessage}")))

  private def convertResponse(
    choice: ChatCompletion.Choice,
    completion: ChatCompletion,
  ): ChatResponse =
    val assistantMsg = choice.message()
    val contents = scala.collection.mutable.ListBuffer[Content]()

    val textContent = assistantMsg.content().orElse("").nn
    if textContent.nonEmpty then
      contents += Content.Text(textContent)

    assistantMsg.toolCalls().orElse(java.util.List.of()).nn.forEach: tc =>
      val func = tc.function().get().nn
      contents += Content.ToolUse(
        id = func.id(),
        name = func.function().name(),
        input = func.function().arguments(),
      )

    val finishReason = choice.finishReason().toString match
      case "stop"       => FinishReason.Stop
      case "length"     => FinishReason.MaxTokens
      case "tool_calls" => FinishReason.ToolUse
      case other        => FinishReason.Other(other)

    val usage =
      if completion.usage().isPresent then
        val u = completion.usage().get().nn
        Some(Usage(
          inputTokens = u.promptTokens(),
          outputTokens = u.completionTokens(),
        ))
      else None

    ChatResponse(
      message = Message(Role.Assistant, contents.toList),
      finishReason = finishReason,
      usage = usage,
    )

  private def convertParameters(params: ToolSchema.Parameters): FunctionParameters =
    val propsMap = new java.util.LinkedHashMap[String, Any]()
    params.properties.foreach: (name, prop) =>
      val propMap = new java.util.LinkedHashMap[String, Any]()
      propMap.put("type", prop.`type`)
      if prop.description.nonEmpty then
        propMap.put("description", prop.description)
      if prop.enumValues.nonEmpty then
        propMap.put("enum", java.util.List.of(prop.enumValues*))
      propsMap.put(name, propMap)

    FunctionParameters.builder()
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
