package capybaraclaw.slack

val Network = munit.Tag("Network")

class SlackBotSuite extends munit.FunSuite:
  private def env(key: String): String =
    sys.env.getOrElse(key, throw RuntimeException(s"$key not set"))

  private lazy val botToken = env("SLACK_BOT_TOKEN")
  private lazy val appToken = env("SLACK_APP_TOKEN")
  private lazy val channel  = env("SLACK_TEST_CHANNEL")

  test("sendMessage posts to channel and returns timestamp".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    val ts = bot.sendMessage(channel, "SlackBotSuite: send test")
    assert(ts != null && ts.nonEmpty, s"Expected a timestamp, got: $ts")

  test("readHistory retrieves recent messages".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    val marker = s"SlackBotSuite: history test ${System.currentTimeMillis()}"
    bot.sendMessage(channel, marker)
    // Brief pause to allow Slack to index the message
    Thread.sleep(1000)
    val messages = bot.readHistory(channel, limit = 5)
    assert(messages.nonEmpty, "Expected at least one message")
    assert(messages.exists(_.text == marker), s"Marker message not found in history: $messages")

  test("echo bot replies to messages".tag(Network)):
    val bot = SlackBot(botToken, appToken)
    val socketModeApp = bot.startEchoBot()
    try
      // Give Socket Mode time to connect
      Thread.sleep(3000)
      val marker = s"echo test ${System.currentTimeMillis()}"
      bot.sendMessage(channel, marker)
      // Wait for the echo reply
      Thread.sleep(3000)
      val messages = bot.readHistory(channel, limit = 5)
      val echoFound = messages.exists: msg =>
        msg.text != null && msg.text.contains(s"Echo: $marker")
      assert(echoFound, s"Echo reply not found in history: ${messages.map(_.text)}")
    finally
      socketModeApp.stop()
