package tacit.library

import io.circe.Decoder
import caps.assumeSafe

@assumeSafe
case class LlmConfig(baseUrl: String, apiKey: String, model: String) derives Decoder:
  override def toString: String = s"LlmConfig($baseUrl, ***, $model)" // hide API key
