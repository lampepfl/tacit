package tacit.library

import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient
import com.openai.models.chat.completions.ChatCompletionCreateParams

class LlmOps(config: Option[LlmConfig]):

  private def requireConfig(): LlmConfig =
    config.getOrElse(
      throw RuntimeException(
        "LLM is not configured. Pass --llm-base-url, --llm-api-key, --llm-model or use a config file.")
    )

  private lazy val client: OpenAIClient =
    val cfg = requireConfig()
    OpenAIOkHttpClient.builder()
      .apiKey(cfg.apiKey)
      .baseUrl(cfg.baseUrl)
      .build()

  def chat(message: String): String =
    val cfg = requireConfig()
    val params = ChatCompletionCreateParams.builder()
      .model(cfg.model)
      .addUserMessage(message)
      .build()
    client.chat().completions().create(params)
      .choices().get(0).message().content().orElse("").nn

  /** The classified overload funnels the call through `map`, so exceptions other
   *  than a missing-config is captured as a `Try.Failure` inside the result.
   *  The failure is only observable when the content is later unwrapped. */
  def chat(message: Classified[String]): Classified[String] =
    requireConfig()
    message.map(chat)
