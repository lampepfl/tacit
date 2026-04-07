package capybaraclaw

import tacit.core.{Context, Config}
import tacit.executor.ReplSession

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*

import capybaraclaw.connectors.slack.SlackBot
import gears.async.Async
import gears.async.default.given
import language.experimental.captureChecking

@main def main(args: String*): Unit = Async.blocking:
  SlackBot.usingBot: bot =>
    val workDir = args.headOption.getOrElse(System.getProperty("user.dir"))
    given Context = Context(Config(restrictedWorkingDir = Some(workDir)), recorder = None)
    given Endpoint = AnthropicEndpoint.createFromEnv()

    val config = AgentConfig(workDir = workDir)
    val repl = ReplSession.create
    val agent = ClawAgent.create(config, repl)

    val onToolCall: (String, String, String) -> Unit = (name, input, result) =>
      println(s" >>> $name")
      println(input)
      println(s" <<< output:\n$result")
      println(" <<< done")

    println("Capybara Claw on Slack. Press Ctrl+C to stop.")

    var running = true
    while running do
      bot.messageChannel.read() match
        case Right(msg) =>
          val user = bot.getUser(msg.userId)
          val channel = bot.getChannel(msg.origin.channelId)
          println(s"[#${channel.name}] ${user.displayName}: ${msg.text}")

          agent.ask(msg.text, onToolCall = Some(onToolCall)) match
            case Right(response) =>
              val thinking = response.message.thinking
              if thinking.nonEmpty then
                println(s"<thinking>\n$thinking\n</thinking>\n")
              println(response.message.text)
              bot.sendMessage(msg.origin.channelId, response.message.text)
              if response.finishReason == FinishReason.MaxTokens then
                println("[max tokens exceeded — response truncated]")
                bot.sendMessage(msg.origin.channelId, "[max tokens exceeded — response truncated]")
            case Left(err) =>
              println(s"Error: $err")
              bot.sendMessage(msg.origin.channelId, s"Error: $err")

        case Left(err) =>
          println(s"Error: $err")
          running = false
