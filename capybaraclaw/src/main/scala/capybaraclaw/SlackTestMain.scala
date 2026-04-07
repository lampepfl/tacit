package capybaraclaw

import capybaraclaw.connectors.slack.{SlackBot, Message}
import gears.async.Async
import gears.async.default.given
import language.experimental.captureChecking

@main def slackTestMain(): Unit = Async.blocking:
  SlackBot.usingBot: bot =>
    println("Echo bot is running. Press Ctrl+C to stop.")
    var running = true
    while running do
      bot.messageChannel.read() match
        case Right(msg) =>
          val user = bot.getUser(msg.userId)
          val channel = bot.getChannel(msg.origin.channelId)
          println(s"[#${channel.name}] ${user.displayName}: ${msg.text}")
          bot.sendMessage(msg.origin.channelId, s"Echo: ${msg.text}")
        case Left(err) =>
          println(s"Error: $err")
          running = false
