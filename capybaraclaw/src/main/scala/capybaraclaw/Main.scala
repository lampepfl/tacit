package capybaraclaw

import tacit.core.{Context, Config}
import tacit.executor.ReplSession

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*

@main def main(): Unit =
  val workDir = System.getProperty("user.dir")
  given Context = Context(Config(restrictedWorkingDir = Some(workDir)), recorder = None)
  given Endpoint = AnthropicEndpoint.createFromEnv()

  val repl = ReplSession.create
  val agent = ClawAgent.create(AgentConfig(), repl)

  val onToolCall: (String, String, String) => Unit = (name, input, result) =>
    println(s" >>> $name")
    println(input)
    println(s" <<< output:\n$result")
    println(" <<< done")

  println("Capybara Claw — Scala REPL Agent (type 'quit' to exit)")
  println()

  var running = true
  while running do
    print("> ")
    val line = scala.io.StdIn.readLine()
    if line == null || line.trim == "quit" then
      running = false
    else if line.trim.nonEmpty then
      agent.ask(line, onToolCall = Some(onToolCall)) match
        case Right(response) =>
          val thinking = response.message.thinking
          if thinking.nonEmpty then
            println(s"<thinking>\n$thinking\n</thinking>\n")
          println(response.message.text)
          if response.finishReason == FinishReason.MaxTokens then
            println("\n[max tokens exceeded — response truncated]")
          println()
        case Left(err) =>
          println(s"Error: $err")
          println()
