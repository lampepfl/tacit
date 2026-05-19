package tacit.library

enum LlmProvider:
  case DeepSeek
  case OpenRouter

object LlmProvider:
  import LlmProvider.*
  def fromId(providerId: String): LlmProvider =
    providerId match
      case "deepseek" => DeepSeek
      case "openrouter" => OpenRouter
      case _ => assert(false, s"Invalid LLM provider: $providerId")

  def baseUrlOf(provider: LlmProvider): String =
    provider match
      case DeepSeek   => "https://api.deepseek.com/v1"
      case OpenRouter => "https://openrouter.ai/api/v1"

  def apiKeyEnvName(provider: LlmProvider): String =
    provider match
      case DeepSeek   => "DEEPSEEK_API_KEY"
      case OpenRouter => "OPENROUTER_API_KEY"

  /** Build an [[LlmConfig]] from a provider id and model name: derive the
   *  base URL from [[baseUrlOf]] and read the API key from the env var named
   *  by [[apiKeyEnvName]]. Throws if the env var is unset. */
  def resolve(providerName: String, modelName: String): LlmConfig =
    val provider = fromId(providerName)
    val envName = apiKeyEnvName(provider)
    val apiKey = sys.env.getOrElse(envName,
      throw RuntimeException(s"$envName environment variable is not set"))
    LlmConfig(baseUrl = baseUrlOf(provider), apiKey = apiKey, model = modelName)
