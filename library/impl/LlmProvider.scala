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
