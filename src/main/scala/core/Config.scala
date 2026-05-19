package tacit
package core

import io.circe.*
import io.circe.parser.decode
import io.circe.syntax.*
import scopt.OParser

enum AgentdojoDomain:
  case Workspace

object AgentdojoDomain:
  val choices: String = values.map(_.toString.toLowerCase).mkString(", ")

  def fromString(s: String): Option[AgentdojoDomain] =
    values.find(_.toString.equalsIgnoreCase(s))

  given Decoder[AgentdojoDomain] = Decoder.decodeString.emap(s =>
    fromString(s).toRight(s"Invalid agentdojo domain: '$s'. Must be one of: $choices")
  )

  given scopt.Read[AgentdojoDomain] = scopt.Read.reads(s =>
    fromString(s).getOrElse(
      throw IllegalArgumentException(s"Invalid agentdojo domain: '$s'. Must be one of: $choices")
    )
  )

case class Config(
  recordPath: Option[String] = None,
  quiet: Boolean = false,
  safeMode: Boolean = true,
  libraryJarPath: String = Option(System.getProperty("tacit.library.jar")).getOrElse(""),
  libraryConfig: Json = Json.obj(),
  agentdojoPort: Option[Int] = None,
  agentdojoDomain: Option[AgentdojoDomain] = None,
  agentdojoSecureChannel: Option[String] = None,
):
  def withLibrary(key: String, value: Json): Config =
    copy(libraryConfig = libraryConfig.deepMerge(Json.obj(key -> value)))

  def withLlm(key: String, value: String): Config =
    val existing = libraryConfig.hcursor.downField("llm").focus.getOrElse(Json.obj())
    withLibrary("llm", existing.deepMerge(Json.obj(key -> value.asJson)))

/** Shape of the JSON config file. All fields optional so missing keys decode as None. */
private case class FileConfig(
  recordPath: Option[String] = None,
  quiet: Option[Boolean] = None,
  safeMode: Option[Boolean] = None,
  libraryJarPath: Option[String] = None,
  libraryConfig: Option[Json] = None,
  agentdojoPort: Option[Int] = None,
  agentdojoDomain: Option[AgentdojoDomain] = None,
  agentdojoSecureChannel: Option[String] = None,
) derives Decoder

object Config:
  private def warn(msg: String): Unit =
    System.err.println(s"[TACIT MCP][config] WARNING: $msg")

  private def readFile(path: String): String =
    val file = java.io.File(path)
    if !file.exists() then throw RuntimeException(s"Config file not found: '$path'")
    if !file.canRead then throw RuntimeException(s"Config file is not readable: '$path'")
    val source = scala.io.Source.fromFile(file)
    try source.mkString finally source.close()

  /** Merges a JSON config file into `base`. Scalar fields come from the file when
   *  present, falling back to `base`; for `libraryConfig` we deep-merge with
   *  `base` on top, so library CLI flags set *before* `--config` still override
   *  file values. CLI flags *after* `--config` override either way, since they
   *  run after this merge completes. */
  private def mergeFromFile(base: Config, path: String): Config =
    val fc = decode[FileConfig](readFile(path)) match
      case Left(err) => throw RuntimeException(s"Failed to parse config file '$path': ${err.getMessage}")
      case Right(fc) => fc
    base.copy(
      recordPath = fc.recordPath.orElse(base.recordPath),
      quiet = fc.quiet.getOrElse(base.quiet),
      safeMode = fc.safeMode.getOrElse(base.safeMode),
      libraryJarPath = fc.libraryJarPath.getOrElse(base.libraryJarPath),
      libraryConfig = fc.libraryConfig.getOrElse(Json.obj()).deepMerge(base.libraryConfig),
      agentdojoPort = base.agentdojoPort.orElse(fc.agentdojoPort),
      agentdojoDomain = base.agentdojoDomain.orElse(fc.agentdojoDomain),
      agentdojoSecureChannel = base.agentdojoSecureChannel.orElse(fc.agentdojoSecureChannel),
    )

  private def validateLlmConfig(config: Config): Config =
    val llm = config.libraryConfig.hcursor.downField("llm")
    val fields = Seq("baseUrl", "apiKey", "model")
    val present = fields.filter(f => llm.get[String](f).toOption.exists(_.nonEmpty))
    val hasProvider = llm.get[String]("provider").toOption.exists(_.nonEmpty)
    if hasProvider then config                       // provider mode — baseUrl/apiKey derived in LlmConfig
    else if present.size == fields.size then config  // all present
    else if present.isEmpty then                     // none present — clean up empty object
      config.copy(libraryConfig = config.libraryConfig.mapObject(_.remove("llm")))
    else
      warn(s"Incomplete LLM config: missing ${(fields.toSet -- present).mkString(", ")}. LLM config ignored.")
      config.copy(libraryConfig = config.libraryConfig.mapObject(_.remove("llm")))

  private def validateLibraryJar(config: Config): Option[Config] =
    if config.libraryJarPath.isEmpty then
      System.err.println("Error: --library-jar is required"); None
    else if !java.io.File(config.libraryJarPath).exists() then
      System.err.println(s"Error: Library JAR not found: '${config.libraryJarPath}'"); None
    else Some(config)

  val optParser =
    val builder = OParser.builder[Config]
    import builder.*
    OParser.sequence(
      programName("TACIT"),
      opt[String]('r', "record")
        .action((x, c) => c.copy(recordPath = Some(x)))
        .text("Record code execution requests in the given directory."),
      opt[Unit]('s', "strict")
        .action((_, c) => c.withLibrary("strictMode", true.asJson))
        .text("Enable strict mode: block file operations (cat, ls, rm, etc.) through exec."),
      opt[String]("classified-paths")
        .action((x, c) => c.withLibrary("classifiedPaths", x.split(",").map(_.trim).filter(_.nonEmpty).toSeq.asJson))
        .text("Comma-separated classified path patterns ('.ssh' matches any .ssh dir, '/abs/path' matches that path)."),
      opt[String]("command-permissions")
        .action((x, c) => c.withLibrary("commandPermissions", x.split(",").map(_.trim).filter(_.nonEmpty).toSeq.asJson))
        .text("Comma-separated glob patterns of exec-able commands (e.g. 'echo,py*'). When set, --strict is ignored."),
      opt[String]("network-permissions")
        .action((x, c) => c.withLibrary("networkPermissions", x.split(",").map(_.trim).filter(_.nonEmpty).toSeq.asJson))
        .text("Comma-separated glob patterns of reachable network hosts (e.g. '*.example.com,api.github.com')."),
      opt[Unit]('q', "quiet")
        .action((_, c) => c.copy(quiet = true))
        .text("Suppress startup banner and request/response logging."),
      opt[Unit]("safe-mode")
        .action((_, c) => c.copy(safeMode = true))
        .text("Enable Scala `language.experimental.safe` in the REPL for every execution (default: on)."),
      opt[Unit]("no-safe-mode")
        .action((_, c) => c.copy(safeMode = false))
        .text("Disable safe mode in the REPL."),
      opt[String]("library-jar")
        .action((x, c) => c.copy(libraryJarPath = x))
        .text("Path to the library JAR (TACIT-library.jar). Required."),
      opt[String]('c', "config")
        .action((x, c) => mergeFromFile(c, x))
        .text("Path to JSON config file."),
      opt[String]("llm-base-url")
        .action((x, c) => c.withLlm("baseUrl", x))
        .text("LLM API base URL."),
      opt[String]("llm-api-key")
        .action((x, c) => c.withLlm("apiKey", x))
        .text("LLM API key."),
      opt[String]("llm-model")
        .action((x, c) => c.withLlm("model", x))
        .text("LLM model name."),
      opt[String]("llm-provider-name")
        .action((x, c) => c.withLlm("provider", x))
        .text("LLM provider name (e.g. 'openrouter', 'deepseek'). Used by the workspace facade together with --llm-model."),
      opt[Int]("agentdojo-port")
        .action((x, c) => c.copy(agentdojoPort = Some(x)))
        .text("Port for the AgentDojo MCP server."),
      opt[AgentdojoDomain]("agentdojo-domain")
        .action((x, c) => c.copy(agentdojoDomain = Some(x)))
        .text(s"AgentDojo domain (${AgentdojoDomain.choices})."),
      opt[String]("agentdojo-secure-channel")
        .action((x, c) => c.copy(agentdojoSecureChannel = Some(x)))
        .text("Path to the secure output file used by AgentDojo domain facades for displaySecurely."),
    )

  def parseCliArgs(args: Array[String]): Option[Config] =
    OParser.parse(optParser, args, Config())
      .map(validateLlmConfig)
      .flatMap(validateLibraryJar)
