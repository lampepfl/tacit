package capybaraclaw

import tacit.agents.llm.endpoint.{EffortLevel, LLMConfig, ThinkingMode}

/** Configuration loaded from claw.json in the working directory. */
case class ClawConfig(
  provider: String = "anthropic",
  model: String = "claude-sonnet-4-6",
  maxTokens: Int = 16000,
  classifiedPaths: List[String] = Nil,
)

object ClawConfig:
  def load(workDir: String): ClawConfig =
    val file = java.io.File(workDir, "claw.json")
    if !file.exists() then return ClawConfig()
    val json = ujson.read(scala.io.Source.fromFile(file).mkString)
    val obj = json.obj
    ClawConfig(
      provider = obj.get("provider").map(_.str).getOrElse("anthropic"),
      model = obj.get("model").map(_.str).getOrElse("claude-sonnet-4-6"),
      maxTokens = obj.get("max_tokens").map(_.num.toInt).getOrElse(16000),
      classifiedPaths = obj.get("classified_paths").map(_.arr.map(_.str).toList).getOrElse(Nil),
    )

/** Internal LLM configuration derived from ClawConfig. */
case class AgentConfig(
  model: String,
  maxTokens: Option[Int],
  thinking: Option[ThinkingMode],
  workDir: String,
  classifiedPaths: List[String] = Nil,
):
  def toLLMConfig: LLMConfig =
    LLMConfig(
      model = model,
      systemPrompt = Some(AgentConfig.buildSystemPrompt(this)),
      maxTokens = maxTokens,
      thinking = thinking,
    )

object AgentConfig:
  def fromClawConfig(cc: ClawConfig, workDir: String): AgentConfig =
    val thinking: Option[ThinkingMode] = cc.provider match
      case "anthropic" => Some(ThinkingMode.Budget(2048))
      case "openai"    => Some(ThinkingMode.Effort(EffortLevel.Medium))
      case _           => None
    AgentConfig(
      model = cc.model,
      maxTokens = Some(cc.maxTokens),
      thinking = thinking,
      workDir = workDir,
      classifiedPaths = cc.classifiedPaths,
    )

  private def loadInterfaceSource(): String =
    val stream = classOf[AgentConfig].getClassLoader.getResourceAsStream("Interface.scala")
    if stream != null then
      try scala.io.Source.fromInputStream(stream).mkString
      finally stream.close()
    else "(Interface.scala not found on classpath)"

  private def loadClawMd(workDir: String): Option[String] =
    val file = java.io.File(workDir, "CLAW.md")
    if file.exists() then
      Some(scala.io.Source.fromFile(file).mkString)
    else None

  private def buildSystemPrompt(config: AgentConfig): String =
    val interfaceSource = loadInterfaceSource()
    val clawMd = loadClawMd(config.workDir)

    val sb = StringBuilder()

    sb.append(s"""<role>
You are a helpful assistant with access to a Scala 3 REPL.
You can evaluate Scala code using the evaluate_scala tool. The REPL session is persistent: definitions and values carry across calls.
</role>

<environment>
Working directory: ${config.workDir}
File system access is restricted to this directory. When using requestFileSystem, always use this path as the root.
</environment>

<config>
$config
</config>

<library_api>
The REPL has the following library API pre-loaded (all functions available at top level):

```scala
$interfaceSource
```
</library_api>""")

    if config.classifiedPaths.nonEmpty then
      sb.append(s"""

<classified_paths>
The following paths should be classified:
${config.classifiedPaths.map(p => s"- $p").mkString("\n")}
</classified_paths>""")

    clawMd.foreach: md =>
      sb.append(s"""

<project_instructions>
$md
</project_instructions>""")

    sb.toString
