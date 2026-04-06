package capybaraclaw.slack

import com.slack.api.Slack
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.ConversationsHistoryRequest
import com.slack.api.bolt.{App, AppConfig}
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.app_backend.events.payload.EventsApiPayload
import com.slack.api.model.event.MessageEvent

case class SlackMessage(user: String | Null, text: String | Null, ts: String | Null)

class SlackBot(botToken: String, appToken: String):
  private val slack = Slack.getInstance()
  private val methods = slack.methods(botToken)

  /** Post a message to a channel. Returns the message timestamp. */
  def sendMessage(channel: String, text: String): String =
    val response = methods.chatPostMessage(
      ChatPostMessageRequest.builder()
        .channel(channel)
        .text(text)
        .build()
    )
    if !response.isOk then
      throw RuntimeException(s"Slack API error: ${response.getError}")
    response.getTs

  /** Read recent messages from a channel. */
  def readHistory(channel: String, limit: Int = 10): List[SlackMessage] =
    val response = methods.conversationsHistory(
      ConversationsHistoryRequest.builder()
        .channel(channel)
        .limit(limit)
        .build()
    )
    if !response.isOk then
      throw RuntimeException(s"Slack API error: ${response.getError}")
    import scala.jdk.CollectionConverters.*
    response.getMessages.asScala.toList.map: msg =>
      SlackMessage(msg.getUser, msg.getText, msg.getTs)

  /** Start a Socket Mode echo bot that replies to every message. */
  def startEchoBot(): SocketModeApp =
    val appConfig = AppConfig.builder()
      .singleTeamBotToken(botToken)
      .build()
    val app = App(appConfig)

    app.event(classOf[MessageEvent], (payload, ctx) => {
      val event = payload.getEvent
      val text = event.getText
      val channel = event.getChannel
      val subtype = event.getSubtype
      // Only echo regular messages (not bot messages, edits, etc.)
      if subtype == null && text != null && channel != null then
        ctx.say(s"Echo: $text")
      ctx.ack()
    })

    val socketModeApp = SocketModeApp(appToken, app)
    socketModeApp.startAsync()
    socketModeApp

object SlackBot:
  def fromEnv(): SlackBot =
    val botToken = sys.env.getOrElse("SLACK_BOT_TOKEN",
      throw RuntimeException("SLACK_BOT_TOKEN not set"))
    val appToken = sys.env.getOrElse("SLACK_APP_TOKEN",
      throw RuntimeException("SLACK_APP_TOKEN not set"))
    SlackBot(botToken, appToken)
