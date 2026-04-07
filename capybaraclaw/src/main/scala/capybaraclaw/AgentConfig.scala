package capybaraclaw

import tacit.agents.llm.endpoint.{LLMConfig, ThinkingMode}

case class AgentConfig(
  model: String = "claude-haiku-4-5",
  maxTokens: Option[Int] = Some(16000),
  thinking: Option[ThinkingMode] = Some(ThinkingMode.Budget(2048)),
  workDir: String = System.getProperty("user.dir"),
):
  def toLLMConfig: LLMConfig =
    LLMConfig(
      model = model,
      systemPrompt = Some(AgentConfig.buildSystemPrompt(this)),
      maxTokens = maxTokens,
      thinking = thinking,
    )

object AgentConfig:
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

    clawMd.foreach: md =>
      sb.append(s"""

<project_instructions>
$md
</project_instructions>""")

    sb.toString
