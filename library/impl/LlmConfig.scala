package tacit.library

import io.circe.Decoder

case class LlmConfig(baseUrl: String, apiKey: String, model: String):
  override def toString: String = s"LlmConfig($baseUrl, ***, $model)" // hide API key

object LlmConfig:
  /** JSON shape: either `{baseUrl, apiKey, model}` (full triple) or
   *  `{provider, model}` (derive baseUrl + apiKey via [[LlmProvider.resolve]]).
   *  When `provider` is present, supplied `baseUrl`/`apiKey` override the
   *  provider defaults. */
  private case class Raw(
    baseUrl: Option[String] = None,
    apiKey: Option[String] = None,
    model: String,
    provider: Option[String] = None
  ) derives Decoder

  given Decoder[LlmConfig] = Decoder[Raw].map: raw =>
    raw.provider match
      case Some(providerName) =>
        val base = LlmProvider.resolve(providerName, raw.model)
        base.copy(
          baseUrl = raw.baseUrl.getOrElse(base.baseUrl),
          apiKey = raw.apiKey.getOrElse(base.apiKey),
        )
      case None =>
        val baseUrl = raw.baseUrl.getOrElse(
          throw RuntimeException("LLM config requires either `provider` or `baseUrl`"))
        val apiKey = raw.apiKey.getOrElse(
          throw RuntimeException("LLM config requires either `provider` or `apiKey`"))
        LlmConfig(baseUrl, apiKey, raw.model)
