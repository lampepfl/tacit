package capybaraclaw

import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.*

import capybaraclaw.connectors.slack.SlackBot
import gears.async.Async
import gears.async.default.given
import language.experimental.captureChecking

@main def main(args: String*): Unit =
  val workDir = args.headOption.getOrElse(System.getProperty("user.dir"))
  val workDirFile = java.io.File(workDir).getCanonicalFile
  if !workDirFile.exists() then
    System.err.println(s"Error: working directory does not exist: $workDirFile")
    sys.exit(1)
  if !workDirFile.isDirectory then
    System.err.println(s"Error: not a directory: $workDirFile")
    sys.exit(1)
  val canonicalWorkDir = workDirFile.getPath
  System.setProperty("user.dir", canonicalWorkDir)

  val claw = ClawAgent(canonicalWorkDir)
  claw.printStartupInfo()

  Async.blocking:
    SlackBot.usingBot: bot =>
      println("Listening on Slack. Press Ctrl+C to stop.")

      var running = true
      while running do
        bot.messageChannel.read() match
          case Right(msg) =>
            val user = bot.getUser(msg.userId)
            val channel = bot.getChannel(msg.origin.channelId)
            println(s"[#${channel.name}] ${user.displayName}: ${msg.text}")

            val toolCalls = scala.collection.mutable.ListBuffer[(String, String, String)]()

            val onToolCall: (String, String, String) -> Unit = (name, input, result) =>
              println(s" >>> $name")
              println(input)
              println(s" <<< output:\n$result")
              println(" <<< done")
              toolCalls += ((name, input, result))

            claw.ask(msg.text, onToolCall = Some(onToolCall)) match
              case Right(response) =>
                val thinking = response.message.thinking
                if thinking.nonEmpty then
                  println(s"<thinking>\n$thinking\n</thinking>\n")
                println(response.message.text)

                val slackMessage = composeSlackMessage(thinking, toolCalls.toList, response)
                bot.sendMessage(msg.origin.channelId, slackMessage)
              case Left(err) =>
                println(s"Error: $err")
                bot.sendMessage(msg.origin.channelId, s"**Error:** $err")

          case Left(err) =>
            println(s"Error: $err")
            running = false

private def composeSlackMessage(
  thinking: String,
  toolCalls: List[(String, String, String)],
  response: ChatResponse,
): String =
  val sb = StringBuilder()

  // Thinking
  if thinking.nonEmpty then
    sb.append("#### Thinking\n")
    sb.append("> ")
    sb.append(thinking.linesIterator.mkString("\n> "))
    sb.append("\n\n")

  // Tool calls
  for (name, input, result) <- toolCalls do
    val code = try
      ujson.read(input)("code").str
    catch
      case _: Exception => input
    sb.append(s"#### $name\n")
    sb.append(s"```scala\n$code\n```\n")
    if result.trim.nonEmpty then
      sb.append(s"> **Output**\n")
      sb.append("> ```\n")
      result.linesIterator.foreach: line =>
        sb.append(s"> $line\n")
      sb.append("> ```\n")
    sb.append("\n")

  if toolCalls.nonEmpty then sb.append("---\n\n")

  // Response
  sb.append(response.message.text)

  if response.finishReason == FinishReason.MaxTokens then
    sb.append("\n\n*[max tokens exceeded — response truncated]*")

  sb.toString
