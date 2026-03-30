package tacit.agents
package llm.endpoint

import com.anthropic.client.AnthropicClient
import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.MessageCreateParams
import com.anthropic.models.messages.ContentBlock
import com.anthropic.models.messages.TextBlock
import com.anthropic.models.messages.ToolUseBlock
import com.anthropic.models.messages.{Message as AnthropicMessage}
import tacit.agents.utils.Result

class AnthropicEndpoint(config: EndpointConfig) extends Endpoint:

  private val DefaultMaxTokens = 4096L

  private lazy val client: AnthropicClient =
    AnthropicOkHttpClient.builder()
      .apiKey(config.apiKey)
      .baseUrl(config.baseUrl)
      .build()

  override def invoke(messages: List[Message], llmConfig: LLMConfig): Result[ChatResponse, LLMError] =
    try
      val maxTokens = llmConfig.maxTokens.map(_.toLong).getOrElse(DefaultMaxTokens)
      val builder = MessageCreateParams.builder()
        .model(llmConfig.model)
        .maxTokens(maxTokens)

      llmConfig.systemPrompt.foreach(p => builder.system(p))

      messages.filterNot(_.role == Role.System).foreach: msg =>
        msg.role match
          case Role.User =>
            builder.addUserMessage(msg.text)
          case Role.Assistant =>
            builder.addAssistantMessage(msg.text)
          case Role.System => ()

      llmConfig.temperature.foreach(t => builder.temperature(t))
      llmConfig.topP.foreach(p => builder.topP(p))
      if llmConfig.stopSequences.nonEmpty then
        llmConfig.stopSequences.foreach(s => builder.addStopSequence(s))

      val response = client.messages().create(builder.build())
      Right(convertResponse(response))
    catch
      case e: Exception =>
        Left(LLMError(s"Anthropic API error: ${e.getMessage}"))

  private def convertResponse(response: AnthropicMessage): ChatResponse =
    val contents = scala.collection.mutable.ListBuffer[Content]()

    response.content().forEach: block =>
      if block.isText then
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

object AnthropicEndpoint extends EndpointProvider:
  type EndpointType = AnthropicEndpoint

  override def create(config: EndpointConfig): AnthropicEndpoint =
    AnthropicEndpoint(config)

  override def createFromEnv(): AnthropicEndpoint =
    val apiKey = sys.env.getOrElse("ANTHROPIC_API_KEY",
      throw RuntimeException("ANTHROPIC_API_KEY environment variable is not set"))
    val baseUrl = sys.env.getOrElse("ANTHROPIC_BASE_URL", "https://api.anthropic.com")
    create(EndpointConfig(baseUrl = baseUrl, apiKey = apiKey))
