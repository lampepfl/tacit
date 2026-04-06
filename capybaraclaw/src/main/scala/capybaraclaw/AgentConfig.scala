package capybaraclaw

import tacit.agents.llm.endpoint.{LLMConfig, ThinkingMode}

case class AgentConfig(
  model: String = "claude-sonnet-4-6",
  maxTokens: Option[Int] = Some(16000),
  thinking: Option[ThinkingMode] = Some(ThinkingMode.Budget(2048)),
  workDir: String = System.getProperty("user.dir"),
):
  def toLLMConfig: LLMConfig =
    LLMConfig(
      model = model,
      systemPrompt = Some(AgentConfig.buildSystemPrompt(workDir)),
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

  private def buildSystemPrompt(workDir: String): String =
    val interfaceSource = loadInterfaceSource()
    s"""You are a helpful assistant with access to a Scala 3 REPL.
You can evaluate Scala code using the evaluate_scala tool. The REPL session is persistent — definitions and values carry across calls.

Your working directory is: $workDir
File system access is restricted to this directory. When using requestFileSystem, always use this path as the root.

The REPL has the following library API pre-loaded (all functions available at top level):

```scala
$interfaceSource
```

Use the REPL to answer questions, run computations, and help the user with Scala programming tasks."""
