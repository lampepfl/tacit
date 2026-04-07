package capybaraclaw

import capybaraclaw.connectors.slack.SlackBot

@main def slackTestMain(): Unit =
  println("Starting Slack Echo Bot...")
  val bot = SlackBot.fromEnv()
  val socketModeApp = bot.startEchoBot()
  println("Echo bot is running. Press Ctrl+C to stop.")

  Runtime.getRuntime.addShutdownHook(Thread(() => {
    println("Shutting down...")
    socketModeApp.stop()
  }))

  // Block main thread
  Thread.currentThread().join()
