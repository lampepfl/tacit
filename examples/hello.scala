//> using scala 3.8.2
//> using repository ivy2local
//> using dep lampepfl:tacit-agents_3:0.1.0-SNAPSHOT
//> using dep ch.epfl.lamp::gears:0.2.0
//> using options -experimental

import tacit.agents.llm.endpoint.*
import gears.async.Async
import gears.async.default.given
import gears.async.Channel

@main def hello(): Unit =
  val endpoint = AnthropicEndpoint.createFromEnv()
  val config = LLMConfig(
    model = "claude-haiku-4-5",
    maxTokens = Some(4096),
    thinking = Some(ThinkingMode.Budget(1024)),
  )

  var history = List.empty[Message]

  println("Chat with Claude (type 'quit' to exit)")
  println()

  var running = true
  while running do
    print("> ")
    val line = scala.io.StdIn.readLine()
    if line == null || line.trim == "quit" then
      running = false
    else
      history = history :+ Message.user(line)
      var inThinking = false
      var lastResponse: ChatResponse | Null = null
      Async.blocking:
        val ch = endpoint.stream(history, config)
        var reading = true
        while reading do
          ch.read() match
            case Right(Right(StreamEvent.ThinkingDelta(text))) =>
              if !inThinking then
                println("<thinking>")
                inThinking = true
              print(text)
              System.out.flush()
            case Right(Right(StreamEvent.Delta(text))) =>
              if inThinking then
                print("\n</thinking>")
                println()
                println()
                inThinking = false
              print(text)
              System.out.flush()
            case Right(Right(StreamEvent.Done(response))) =>
              lastResponse = response
            case Right(Left(err)) =>
              println(s"\nError: $err")
            case Left(_) =>
              reading = false
            case _ => ()
      println()
      println()
      if lastResponse != null then
        history = history :+ Message.assistant(lastResponse.nn.message.text)
