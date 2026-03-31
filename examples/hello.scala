//> using scala 3.8.2
//> using repository ivy2local
//> using dep lampepfl:tacit-agents_3:0.1.0-SNAPSHOT
//> using dep ch.epfl.lamp::gears:0.2.0
//> using dep com.lihaoyi::ujson:4.4.3
//> using options -experimental

import tacit.agents.llm.endpoint.*
import gears.async.Async
import gears.async.default.given
import gears.async.Channel

val rollDicesTool = ToolSchema(
  name = "rollDices",
  description = "Roll a number of six-sided dices and return the results",
  parameters = ToolSchema.Parameters(
    properties = Map(
      "count" -> ToolSchema.Property(`type` = "integer", description = "The number of dices to roll"),
    ),
    required = List("count"),
  ),
)

def handleToolCall(name: String, input: String): String =
  name match
    case "rollDices" =>
      val count = ujson.read(input)("count").num.toInt
      val results = (1 to count).map(_ => scala.util.Random.nextInt(6) + 1)
      s"""{"results": [${results.mkString(", ")}]}"""
    case other =>
      s"""{"error": "Unknown tool: $other"}"""

def streamResponse(endpoint: Endpoint, history: List[Message], config: LLMConfig): ChatResponse =
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
  if inThinking then
    print("\n</thinking>")
    println()
    println()
  lastResponse.nn

@main def runLLM(): Unit =
  val endpoint = OpenAIEndpoint.createFromEnv()
  val config = LLMConfig(
    model = "gpt-5.4",
    maxTokens = Some(4096),
    thinking = Some(ThinkingMode.Effort(EffortLevel.High)),
    tools = List(rollDicesTool),
  )

  var history = List.empty[Message]

  println("Chat with GPT (type 'quit' to exit)")
  println()

  var running = true
  while running do
    print("> ")
    val line = scala.io.StdIn.readLine()
    if line == null || line.trim == "quit" then
      running = false
    else
      history = history :+ Message.user(line)

      var response = streamResponse(endpoint, history, config)
      history = history :+ response.message

      // Tool call loop
      while response.finishReason == FinishReason.ToolUse do
        val toolUses = response.message.content.collect { case c: Content.ToolUse => c }
        val toolResults = toolUses.map: tu =>
          val result = handleToolCall(tu.name, tu.input)
          println(s"[tool: ${tu.name}] $result")
          Content.ToolResult(tu.id, result)
        history = history :+ Message(Role.User, toolResults)
        response = streamResponse(endpoint, history, config)
        history = history :+ response.message

      println()
      println()
