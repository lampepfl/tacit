package tacit.agents
package llm.endpoint

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.{
  MessageCreateParams, ContentBlock, ContentBlockParam, TextBlock, TextBlockParam,
  ToolUseBlock, ToolUseBlockParam, ToolResultBlockParam,
  RawMessageStreamEvent, ThinkingConfigParam, ThinkingConfigEnabled, ThinkingConfigDisabled,
  Message as AnthropicMessage, Tool as AnthropicTool,
}
import com.anthropic.core.JsonValue
import scala.jdk.CollectionConverters.*
import gears.async.{Async, Future, BufferedChannel, ReadableChannel}
import tacit.agents.utils.Result

/** Anthropic API endpoint. */
class AnthropicEndpoint(config: EndpointConfig) extends Endpoint:

  private val DefaultMaxTokens = 4096L

  private lazy val client: AnthropicClient =
    AnthropicOkHttpClient.builder()
      .apiKey(config.apiKey)
      .baseUrl(config.baseUrl)
      .build()

  private def buildParams(messages: List[Message], llmConfig: LLMConfig): MessageCreateParams.Builder =
    val maxTokens = llmConfig.maxTokens.map(_.toLong).getOrElse(DefaultMaxTokens)
    val builder = MessageCreateParams.builder()
      .model(llmConfig.model)
      .maxTokens(maxTokens)

    llmConfig.systemPrompt.foreach(p => builder.system(p))

    messages.filterNot(_.role == Role.System).foreach: msg =>
      msg.role match
        case Role.User =>
          val hasToolResults = msg.content.exists(_.isInstanceOf[Content.ToolResult])
          if hasToolResults then
            val blocks = msg.content.map:
              case Content.ToolResult(toolUseId, content, isError) =>
                ContentBlockParam.ofToolResult(
                  ToolResultBlockParam.builder()
                    .toolUseId(toolUseId)
                    .content(content)
                    .isError(isError)
                    .build()
                )
              case Content.Text(text) =>
                ContentBlockParam.ofText(TextBlockParam.builder().text(text).build())
              case other =>
                ContentBlockParam.ofText(TextBlockParam.builder().text(other.toString).build())
            builder.addUserMessageOfBlockParams(blocks.asJava)
          else
            builder.addUserMessage(msg.text)
        case Role.Assistant =>
          val hasToolUse = msg.content.exists(_.isInstanceOf[Content.ToolUse])
          if hasToolUse then
            val blocks = msg.content.map:
              case Content.ToolUse(id, name, input) =>
                val mapper = com.fasterxml.jackson.databind.json.JsonMapper.builder().nn.build().nn
                val tree = mapper.readTree(input).nn
                val inputBuilder = ToolUseBlockParam.Input.builder()
                tree.fields().nn.forEachRemaining: entry =>
                  inputBuilder.putAdditionalProperty(entry.getKey, JsonValue.fromJsonNode(entry.getValue))
                ContentBlockParam.ofToolUse(
                  ToolUseBlockParam.builder()
                    .id(id)
                    .name(name)
                    .input(inputBuilder.build())
                    .build()
                )
              case Content.Text(text) =>
                ContentBlockParam.ofText(TextBlockParam.builder().text(text).build())
              case Content.Thinking(text) =>
                ContentBlockParam.ofText(TextBlockParam.builder().text(text).build())
              case other =>
                ContentBlockParam.ofText(TextBlockParam.builder().text(other.toString).build())
            builder.addAssistantMessageOfBlockParams(blocks.asJava)
          else
            builder.addAssistantMessage(msg.text)
        case Role.System => ()

    llmConfig.temperature.foreach(t => builder.temperature(t))
    llmConfig.topP.foreach(p => builder.topP(p))
    if llmConfig.stopSequences.nonEmpty then
      llmConfig.stopSequences.foreach(s => builder.addStopSequence(s))

    if llmConfig.tools.nonEmpty then
      llmConfig.tools.foreach: tool =>
        builder.addTool(
          AnthropicTool.builder()
            .name(tool.name)
            .description(tool.description)
            .inputSchema(convertInputSchema(tool.parameters))
            .build()
        )

    llmConfig.thinking.foreach:
      case ThinkingMode.Disabled =>
        builder.thinking(ThinkingConfigParam.ofDisabled(ThinkingConfigDisabled.builder().build()))
      case ThinkingMode.Auto =>
        builder.thinking(ThinkingConfigParam.ofAdaptive(
          com.anthropic.models.messages.ThinkingConfigAdaptive.builder().build()
        ))
      case ThinkingMode.Budget(n) =>
        builder.thinking(ThinkingConfigParam.ofEnabled(
          ThinkingConfigEnabled.builder().budgetTokens(n.toLong).build()
        ))
      case ThinkingMode.Effort(_) =>
        throw IllegalArgumentException("Effort levels are not valid for Anthropic. Use ThinkingMode.Budget or ThinkingMode.Auto.")

    builder

  override def invoke(messages: List[Message], llmConfig: LLMConfig): Result[ChatResponse, LLMError] =
    try
      val response = client.messages().create(buildParams(messages, llmConfig).build())
      Right(convertResponse(response))
    catch
      case e: Exception =>
        Left(LLMError(s"Anthropic API error: ${e.getMessage}"))

  override def stream(messages: List[Message], llmConfig: LLMConfig)(using Async.Spawn): ReadableChannel[Result[StreamEvent, LLMError]] =
    val ch = BufferedChannel[Result[StreamEvent, LLMError]](16)
    Future:
      try
        val params = buildParams(messages, llmConfig).build()
        val streamResponse = client.messages().createStreaming(params)
        val iterator = streamResponse.stream().iterator().asScala

        // Accumulators
        val thinkingBuf = new StringBuilder
        val textBuf = new StringBuilder
        val toolCalls = scala.collection.mutable.Map[Int, (String, String, StringBuilder)]()
        var blockIndex = 0
        var lastFinishReason: FinishReason = FinishReason.Stop
        var lastUsage: Option[Usage] = None

        while iterator.hasNext do
          val event = iterator.next()

          if event.isContentBlockStart then
            val startEvent = event.asContentBlockStart()
            val block = startEvent.contentBlock()
            val idx = startEvent.index().toInt
            blockIndex = idx
            if block.isToolUse then
              val tu = block.asToolUse()
              toolCalls(idx) = (tu.id(), tu.name(), new StringBuilder)
              ch.send(Right(StreamEvent.ToolCallStart(idx, tu.id(), tu.name())))

          else if event.isContentBlockDelta then
            val deltaEvent = event.asContentBlockDelta()
            val idx = deltaEvent.index().toInt
            val delta = deltaEvent.delta()
            if delta.isThinking then
              val thinking = delta.asThinking().thinking()
              thinkingBuf.append(thinking)
              ch.send(Right(StreamEvent.ThinkingDelta(thinking)))
            else if delta.isText then
              val text = delta.asText().text()
              textBuf.append(text)
              ch.send(Right(StreamEvent.Delta(text)))
            else if delta.isInputJson then
              val json = delta.asInputJson().partialJson()
              toolCalls.get(idx).foreach: (_, _, buf) =>
                buf.append(json)
              ch.send(Right(StreamEvent.ToolCallDelta(idx, json)))

          else if event.isMessageDelta then
            val md = event.asMessageDelta()
            md.delta().stopReason().ifPresent: reason =>
              lastFinishReason = reason.toString match
                case "end_turn"   => FinishReason.Stop
                case "max_tokens" => FinishReason.MaxTokens
                case "tool_use"   => FinishReason.ToolUse
                case other        => FinishReason.Other(other)
            lastUsage = Some(Usage(
              inputTokens = md.usage().inputTokens().orElse(0L),
              outputTokens = md.usage().outputTokens(),
            ))

        streamResponse.close()
        val contents = scala.collection.mutable.ListBuffer[Content]()
        if thinkingBuf.nonEmpty then
          contents += Content.Thinking(thinkingBuf.toString)
        if textBuf.nonEmpty then
          contents += Content.Text(textBuf.toString)
        toolCalls.toList.sortBy(_._1).foreach: (_, tuple) =>
          contents += Content.ToolUse(tuple._1, tuple._2, tuple._3.toString)
        val response = ChatResponse(
          message = Message(Role.Assistant, contents.toList),
          finishReason = lastFinishReason,
          usage = lastUsage,
        )
        ch.send(Right(StreamEvent.Done(response)))
        ch.close()
      catch
        case e: Exception =>
          ch.send(Left(LLMError(s"Anthropic API error: ${e.getMessage}")))
          // Do not close the channel here — BufferedChannel.close() discards
          // buffered messages, so the consumer would see Left(Closed) instead
          // of the LLMError we just sent.
    ch.asReadable

  private def convertResponse(response: AnthropicMessage): ChatResponse =
    val contents = scala.collection.mutable.ListBuffer[Content]()

    response.content().forEach: block =>
      if block.isThinking then
        val thinking = block.asThinking().thinking()
        if thinking.nonEmpty then
          contents += Content.Thinking(thinking)
      else if block.isText then
        contents += Content.Text(block.asText().text())
      else if block.isToolUse then
        val tu = block.asToolUse()
        contents += Content.ToolUse(
          id = tu.id(),
          name = tu.name(),
          input = tu._input().toString,
        )

    val finishReason =
      if response.stopReason().isPresent then
        response.stopReason().get().nn.toString match
          case "end_turn"   => FinishReason.Stop
          case "max_tokens" => FinishReason.MaxTokens
          case "tool_use"   => FinishReason.ToolUse
          case other        => FinishReason.Other(other)
      else FinishReason.Stop

    val usage = Usage(
      inputTokens = response.usage().inputTokens(),
      outputTokens = response.usage().outputTokens(),
    )

    ChatResponse(
      message = Message(Role.Assistant, contents.toList),
      finishReason = finishReason,
      usage = Some(usage),
    )

  private def convertInputSchema(params: ToolSchema.Parameters): AnthropicTool.InputSchema =
    val propsBuilder = AnthropicTool.InputSchema.Properties.builder()
    params.properties.foreach: (name, prop) =>
      val propMap = new java.util.LinkedHashMap[String, Any]()
      propMap.put("type", prop.`type`)
      if prop.description.nonEmpty then
        propMap.put("description", prop.description)
      if prop.enumValues.nonEmpty then
        propMap.put("enum", java.util.List.of(prop.enumValues*))
      prop.items.foreach: itemProp =>
        val itemMap = new java.util.LinkedHashMap[String, Any]()
        itemMap.put("type", itemProp.`type`)
        propMap.put("items", itemMap)
      propsBuilder.putAdditionalProperty(name, JsonValue.from(propMap))

    AnthropicTool.InputSchema.builder()
      .properties(propsBuilder.build())
      .required(params.required.asJava)
      .build()

object AnthropicEndpoint extends EndpointProvider:
  type EndpointType = AnthropicEndpoint

  override def create(config: EndpointConfig): AnthropicEndpoint =
    AnthropicEndpoint(config)

  override def createFromEnv(): AnthropicEndpoint =
    val apiKey = sys.env.getOrElse("ANTHROPIC_API_KEY",
      throw RuntimeException("ANTHROPIC_API_KEY environment variable is not set"))
    val baseUrl = sys.env.getOrElse("ANTHROPIC_BASE_URL", "https://api.anthropic.com")
    create(EndpointConfig(baseUrl = baseUrl, apiKey = apiKey))
