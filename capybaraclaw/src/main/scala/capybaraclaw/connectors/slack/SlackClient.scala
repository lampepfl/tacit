package capybaraclaw.connectors.slack

import com.slack.api.Slack
import com.slack.api.methods.MethodsClient
import com.slack.api.methods.request.chat.ChatPostMessageRequest
import com.slack.api.methods.request.conversations.{ConversationsHistoryRequest, ConversationsInfoRequest}
import com.slack.api.methods.request.users.UsersInfoRequest
import com.slack.api.bolt.{App, AppConfig}
import com.slack.api.bolt.socket_mode.SocketModeApp
import com.slack.api.model.event.MessageEvent
import gears.async.{ReadableChannel, UnboundedChannel}
import scala.jdk.CollectionConverters.*
import scala.collection.mutable

// --- ADTs ---

case class Channel(
  id: String,
  name: String,
  topic: String,
  purpose: String,
  isPrivate: Boolean,
  isIm: Boolean,
  isArchived: Boolean,
)

object Channel:
  def fromConversation(c: com.slack.api.model.Conversation): Channel =
    Channel(
      id = c.getId.nn,
      name = Option(c.getName).getOrElse(""),
      topic = Option(c.getTopic).map(_.getValue.nn).getOrElse(""),
      purpose = Option(c.getPurpose).map(_.getValue.nn).getOrElse(""),
      isPrivate = c.isPrivate,
      isIm = c.isIm,
      isArchived = c.isArchived,
    )

case class User(
  id: String,
  name: String,
  realName: String,
  displayName: String,
)

object User:
  def fromSdkUser(u: com.slack.api.model.User): User =
    User(
      id = u.getId.nn,
      name = Option(u.getName).getOrElse(""),
      realName = Option(u.getRealName).getOrElse(""),
      displayName = Option(u.getProfile).flatMap(p => Option(p.getDisplayName)).getOrElse(""),
    )

enum MessageOrigin(val channelId: String):
  case DirectMessage(override val channelId: String) extends MessageOrigin(channelId)
  case GroupMessage(override val channelId: String) extends MessageOrigin(channelId)
  case ChannelMessage(override val channelId: String) extends MessageOrigin(channelId)

object MessageOrigin:
  def fromChannelType(channelType: String | Null, channelId: String): MessageOrigin =
    channelType match
      case "im" | "direct_message" => DirectMessage(channelId)
      case "mpim" | "group" => GroupMessage(channelId)
      case _ => ChannelMessage(channelId)

case class Message(
  userId: String,
  text: String,
  ts: String,
  threadTs: Option[String],
  origin: MessageOrigin,
)

// --- Client ---

class SlackClient(botToken: String, appToken: String):
  private val slack = Slack.getInstance()
  private val methods: MethodsClient = slack.methods(botToken)

  private val channelCache = mutable.Map[String, Channel]()
  private val userCache = mutable.Map[String, User]()

  /** Resolve a channel by ID (cached). */
  def getChannel(id: String): Channel =
    channelCache.getOrElseUpdate(id, fetchChannel(id))

  /** Resolve a user by ID (cached). */
  def getUser(id: String): User =
    userCache.getOrElseUpdate(id, fetchUser(id))

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
    response.getTs.nn

  /** Read recent messages from a channel. */
  def readHistory(channel: String, limit: Int = 32): List[Message] =
    val response = methods.conversationsHistory(
      ConversationsHistoryRequest.builder()
        .channel(channel)
        .limit(limit)
        .build()
    )
    if !response.isOk then
      throw RuntimeException(s"Slack API error: ${response.getError}")
    response.getMessages.nn.asScala.toList.map: msg =>
      Message(
        userId = Option(msg.getUser).getOrElse(""),
        text = Option(msg.getText).getOrElse(""),
        ts = Option(msg.getTs).getOrElse(""),
        threadTs = Option(msg.getThreadTs),
        origin = MessageOrigin.ChannelMessage(channel),
      )

  // --- Socket Mode listener (starts immediately) ---

  private val incomingMessages = UnboundedChannel[Message]()
  private val socketModeApp: SocketModeApp =
    val appConfig = AppConfig.builder()
      .singleTeamBotToken(botToken)
      .build()
    val app = App(appConfig)

    app.event(classOf[MessageEvent], (payload, ctx) => {
      val event = payload.getEvent.nn
      val text = event.getText
      val channel = event.getChannel
      val subtype = event.getSubtype
      if subtype == null && text != null && channel != null then
        val msg = Message(
          userId = Option(event.getUser).getOrElse(""),
          text = text,
          ts = Option(event.getTs).getOrElse(""),
          threadTs = Option(event.getThreadTs),
          origin = MessageOrigin.fromChannelType(event.getChannelType, channel),
        )
        incomingMessages.sendImmediately(msg)
      ctx.ack()
    })

    val sma = SocketModeApp(appToken, app)
    sma.startAsync()
    sma

  /** Channel of incoming messages. */
  def messageChannel: ReadableChannel[Message] = incomingMessages.asReadable

  /** Shut down the Socket Mode connection. */
  def shutdown(): Unit = socketModeApp.stop()

  private def fetchChannel(id: String): Channel =
    val response = methods.conversationsInfo(
      ConversationsInfoRequest.builder().channel(id).build()
    )
    if !response.isOk then
      throw RuntimeException(s"Slack API error (conversations.info): ${response.getError}")
    Channel.fromConversation(response.getChannel.nn)

  private def fetchUser(id: String): User =
    val response = methods.usersInfo(
      UsersInfoRequest.builder().user(id).build()
    )
    if !response.isOk then
      throw RuntimeException(s"Slack API error (users.info): ${response.getError}")
    User.fromSdkUser(response.getUser.nn)
