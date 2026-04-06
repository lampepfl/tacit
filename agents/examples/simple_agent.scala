//> using scala 3.8.2
//> using repository ivy2local
//> using dep lampepfl:tacit-agents_3:0.1.0-SNAPSHOT
//> using dep ch.epfl.lamp::gears:0.2.0
//> using options -experimental

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*
import tacit.agents.llm.utils.IsToolArg
import gears.async.Async
import gears.async.default.given

case class DiceArgs(count: Int) derives IsToolArg
case class WeatherArgs(city: String) derives IsToolArg
case class CalcArgs(expression: String) derives IsToolArg
case class CoinFlipArgs(count: Int) derives IsToolArg
case class CountdownArgs(from: Int) derives IsToolArg

@main def runAgent(): Unit =
  given Endpoint = AnthropicEndpoint.createFromEnv()

  val agent = new Agent:
    type State = AgentState
    def getInitState = new AgentState:
      val llmConfig = LLMConfig(
        model = "claude-haiku-4-5-20251001",
        maxTokens = Some(4096),
        thinking = Some(ThinkingMode.Budget(1024)),
      )

  agent
    .handle[DiceArgs]("rollDice", "Roll a number of six-sided dice and return the results"): (args, _) =>
      val results = (1 to args.count).map(_ => scala.util.Random.nextInt(6) + 1)
      s"""{"results": [${results.mkString(", ")}]}"""

    .handle[WeatherArgs]("getWeather", "Get the current weather for a city (simulated)"): (args, _) =>
      val temp = 15 + scala.util.Random.nextInt(20)
      val conditions = List("sunny", "cloudy", "rainy", "windy", "snowy")
      val condition = conditions(scala.util.Random.nextInt(conditions.size))
      s"""{"city": "${args.city}", "temperature": $temp, "condition": "$condition"}"""

    .handle[CalcArgs]("calculate", "Evaluate a simple arithmetic expression"): (args, _) =>
      try
        val result = javax.script.ScriptEngineManager().getEngineByName("js").eval(args.expression)
        s"""{"result": $result}"""
      catch
        case e: Exception => s"""{"error": "${e.getMessage}"}"""

    .handle[CoinFlipArgs]("flipCoins", "Flip a number of coins and return heads/tails results"): (args, _) =>
      val results = (1 to args.count).map(_ => if scala.util.Random.nextBoolean() then "heads" else "tails")
      s"""{"results": [${results.map(r => s""""$r"""").mkString(", ")}]}"""

    .handle[CountdownArgs]("countdown", "Count down from a number, returning each step"): (args, _) =>
      val steps = (args.from to 0 by -1).toList
      s"""{"steps": [${steps.mkString(", ")}], "message": "Liftoff!"}"""

  println("Chat with Claude (type 'quit' to exit)")
  println()

  var running = true
  while running do
    print("> ")
    val line = scala.io.StdIn.readLine()
    if line == null || line.trim == "quit" then
      running = false
    else
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
              println(s"[tool: $toolName] $result")
            case Right(Left(err)) =>
              println(s"\nError: $err")
            case Left(_) =>
              reading = false
            case _ => ()
        if inThinking then
          print("\n</thinking>\n\n")
        println()
        println()
