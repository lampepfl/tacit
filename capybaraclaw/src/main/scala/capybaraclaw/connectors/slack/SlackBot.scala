package capybaraclaw.connectors.slack
import language.experimental.captureChecking

import gears.async.ReadableChannel

class SlackBot(botToken: String, appToken: String):
  val client: SlackClient = SlackClient(botToken, appToken)

  def sendMessage(channel: String, text: String): String =
    client.sendMessage(channel, text)

  def readHistory(channel: String, limit: Int = 32): List[Message] =
    client.readHistory(channel, limit)

  def getChannel(id: String): Channel = client.getChannel(id)
  def getUser(id: String): User = client.getUser(id)

  def messageChannel: ReadableChannel[Message] = client.messageChannel
  def shutdown(): Unit = client.shutdown()

object SlackBot:
  def fromEnv(): SlackBot =
    val botToken = sys.env.getOrElse("SLACK_BOT_TOKEN",
      throw RuntimeException("SLACK_BOT_TOKEN not set"))
    val appToken = sys.env.getOrElse("SLACK_APP_TOKEN",
      throw RuntimeException("SLACK_APP_TOKEN not set"))
    SlackBot(botToken, appToken)

  def usingBot[R](block: SlackBot^ => R): R =
    val bot = fromEnv()
    try block(bot)
    finally bot.shutdown()

