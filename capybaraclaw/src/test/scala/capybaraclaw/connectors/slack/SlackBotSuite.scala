package capybaraclaw.connectors.slack

import gears.async.Async
import gears.async.default.given

val Network = munit.Tag("Network")

class SlackBotSuite extends munit.FunSuite:
  private def env(key: String): String =
    sys.env.getOrElse(key, throw RuntimeException(s"$key not set"))

  private lazy val botToken = env("SLACK_BOT_TOKEN")
  private lazy val appToken = env("SLACK_APP_TOKEN")
  private lazy val channel  = env("SLACK_TEST_CHANNEL")

  test("sendMessage posts to channel and returns timestamp".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    try
      val ts = bot.sendMessage(channel, "SlackBotSuite: send test")
      assert(ts.nonEmpty, s"Expected a timestamp, got: $ts")
    finally bot.shutdown()

  test("readHistory retrieves recent messages".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    try
      val marker = s"SlackBotSuite: history test ${System.currentTimeMillis()}"
      bot.sendMessage(channel, marker)
      Thread.sleep(1000)
      val messages = bot.readHistory(channel, limit = 5)
      assert(messages.nonEmpty, "Expected at least one message")
      assert(messages.exists(_.text == marker), s"Marker message not found in history: $messages")
    finally bot.shutdown()

  test("messageChannel receives messages".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    try
      Async.blocking:
        Thread.sleep(3000) // wait for Socket Mode to connect
        val marker = s"listen test ${System.currentTimeMillis()}"
        bot.sendMessage(channel, marker)
        Thread.sleep(3000)
        val history = bot.readHistory(channel, limit = 5)
        val found = history.exists(_.text.contains(marker))
        assert(found, s"Marker not found in history: ${history.map(_.text)}")
    finally bot.shutdown()
