package capybaraclaw

import tacit.core.{Context, Config}
import tacit.executor.ReplSession

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*
import tacit.agents.llm.utils.IsToolArg
import gears.async.Async
import gears.async.default.given

case class EvalScalaArgs(code: String) derives IsToolArg

@main def main(): Unit =
  given Context = Context(Config(), recorder = None)
  given Endpoint = AnthropicEndpoint.createFromEnv()

  val repl = ReplSession.create

  val interfaceSource =
    val stream = classOf[EvalScalaArgs].getClassLoader.getResourceAsStream("Interface.scala")
    if stream != null then
      try scala.io.Source.fromInputStream(stream).mkString
      finally stream.close()
    else "(Interface.scala not found on classpath)"

  val systemPrompt = s"""You are a helpful assistant with access to a Scala 3 REPL.
You can evaluate Scala code using the evaluate_scala tool. The REPL session is persistent — definitions and values carry across calls.

The REPL has the following library API pre-loaded (all functions available at top level):

```scala
$interfaceSource
```

Use the REPL to answer questions, run computations, and help the user with Scala programming tasks."""

  val agent = new Agent:
    type State = AgentState
    def getInitState = new AgentState:
      val llmConfig = LLMConfig(
        model = "claude-sonnet-4-20250514",
        systemPrompt = Some(systemPrompt),
        maxTokens = Some(4096),
      )

  agent.handle[EvalScalaArgs]("evaluate_scala", "Evaluate a Scala expression in a persistent REPL session"): (args, _) =>
    val result = repl.execute(args.code)
    if result.success then
      if result.output.nonEmpty then result.output
      else "(executed successfully, no output)"
    else
      val msg = StringBuilder("Execution failed.\n")
      if result.output.nonEmpty then msg.append(s"Output:\n${result.output}\n")
      result.error.foreach(e => msg.append(s"Error:\n$e\n"))
      msg.toString

  println("Capybara Claw — Scala REPL Agent (type 'quit' to exit)")
  println()

  var running = true
  while running do
    print("> ")
    val line = scala.io.StdIn.readLine()
    if line == null || line.trim == "quit" then
      running = false
    else if line.trim.nonEmpty then
      Async.blocking:
        var inThinking = false
        val ch = agent.streamAsk(line)
        var reading = true
        while reading do
          ch.read() match
            case Right(Right(AgentStreamEvent.Stream(StreamEvent.ThinkingDelta(text)))) =>
              if !inThinking then
                println("<thinking>")
                inThinking = true
              print(text)
              System.out.flush()
            case Right(Right(AgentStreamEvent.Stream(StreamEvent.Delta(text)))) =>
              if inThinking then
                print("\n</thinking>\n\n")
                inThinking = false
              print(text)
              System.out.flush()
            case Right(Right(AgentStreamEvent.ToolResult(_, toolName, result))) =>
              println(s"\n[$toolName] $result")
            case Right(Left(err)) =>
              println(s"\nError: $err")
            case Left(_) =>
              reading = false
            case _ => ()
        if inThinking then
          print("\n</thinking>\n\n")
        println()
        println()
