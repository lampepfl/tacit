package tacit.library

import io.circe.*
import io.circe.parser.decode
import caps.assumeSafe

@assumeSafe
case class LibraryConfig(
  strictMode: Option[Boolean] = None,
  classifiedPaths: Option[Set[String]] = None,
  commandPermissions: Option[Set[String]] = None,
  networkPermissions: Option[Set[String]] = None,
  llm: Option[LlmConfig] = None,
  secureOutput: Option[String] = None
) derives Decoder

@assumeSafe
object LibraryConfig:
  def fromJson(json: String): LibraryConfig =
    if json.isBlank || json.trim == "{}" then return LibraryConfig()
    decode[LibraryConfig](json) match
      case Left(err) => throw RuntimeException(s"Failed to parse library config: ${err.getMessage}")
      case Right(cfg) => cfg
