//> using scala 3.8.2
//> using repository ivy2local
//> using dep lampepfl:tacit-agents_3:0.1.0-SNAPSHOT
//> using options -experimental

import tacit.agents.llm.endpoint.*

@main def hello(): Unit =
  val endpoint = AnthropicEndpoint.createFromEnv()
  val config = LLMConfig(model = "claude-haiku-4-5", maxTokens = Some(1024))

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
      val result = endpoint.invoke(history, config)
      result match
        case Right(response) =>
          val text = response.message.text
          println(text)
          println()
          history = history :+ Message.assistant(text)
        case Left(err) =>
          println(s"Error: $err")
          println()
