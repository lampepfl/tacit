package capybaraclaw

import tacit.core.{Context, Config}
import tacit.executor.ReplSession

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*
import gears.async.Async
import gears.async.default.given

@main def main(): Unit =
  val workDir = System.getProperty("user.dir")
  given Context = Context(Config(restrictedWorkingDir = Some(workDir)), recorder = None)
  given Endpoint = AnthropicEndpoint.createFromEnv()

  val repl = ReplSession.create
  val agent = ClawAgent.create(AgentConfig(), repl)

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
        var inToolCall = false
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
            case Right(Right(AgentStreamEvent.Stream(StreamEvent.ToolCallStart(_, _, name)))) =>
              if inThinking then
                print("\n</thinking>\n\n")
                inThinking = false
              println(s" >>> $name")
              inToolCall = true
            case Right(Right(AgentStreamEvent.Stream(StreamEvent.ToolCallDelta(_, delta)))) =>
              print(delta)
              System.out.flush()
            case Right(Right(AgentStreamEvent.ToolResult(_, _, result))) =>
              if inToolCall then
                println()
                inToolCall = false
              println(s" <<< output:\n$result")
              println(" <<< done")
            case Right(Right(AgentStreamEvent.MaxTokensExceeded)) =>
              if inToolCall then
                println()
                inToolCall = false
              println("\n[max tokens exceeded — response truncated]")
            case Right(Left(err)) =>
              println(s"\nError: $err")
            case Left(_) =>
              reading = false
            case _ => ()
        if inThinking then
          print("\n</thinking>\n\n")
        println()
        println()
