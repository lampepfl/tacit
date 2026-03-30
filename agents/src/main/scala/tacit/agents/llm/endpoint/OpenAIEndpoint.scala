package tacit.agents
package llm.endpoint

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletion
import com.openai.models.chat.completions.ChatCompletionCreateParams
import com.openai.models.chat.completions.ChatCompletionToolMessageParam
import tacit.agents.utils.Result

class OpenAIEndpoint(config: EndpointConfig) extends Endpoint:

  private lazy val client: OpenAIClient =
    OpenAIOkHttpClient.builder()
      .apiKey(config.apiKey)
      .baseUrl(config.baseUrl)
      .build()

  override def invoke(messages: List[Message], llmConfig: LLMConfig): Result[ChatResponse, LLMError] =
    try
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

      val completion = client.chat().completions().create(builder.build())
      val choice = completion.choices().get(0)
      Right(convertResponse(choice, completion))
    catch
      case e: Exception =>
        Left(LLMError(s"OpenAI API error: ${e.getMessage}"))

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

object OpenAIEndpoint extends EndpointProvider:
  type EndpointType = OpenAIEndpoint

  override def create(config: EndpointConfig): OpenAIEndpoint =
    OpenAIEndpoint(config)

  override def createFromEnv(): OpenAIEndpoint =
    val apiKey = sys.env.getOrElse("OPENAI_API_KEY",
      throw RuntimeException("OPENAI_API_KEY environment variable is not set"))
    val baseUrl = sys.env.getOrElse("OPENAI_BASE_URL", "https://api.openai.com/v1")
    create(EndpointConfig(baseUrl = baseUrl, apiKey = apiKey))
