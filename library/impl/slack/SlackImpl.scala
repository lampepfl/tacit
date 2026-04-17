package tacit.library.slack

import language.experimental.captureChecking
import caps.*

import tacit.library.{Classified, ClassifiedImpl, IOCapability, LlmConfig, LlmOps}
import tacit.library.mcp.{JValue, MCPClient, MCPError}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}

@assumeSafe
class SlackImpl(endpoint: String, secureOutputPath: String) extends SlackService, AutoCloseable:
  private val client = MCPClient(endpoint)

  client.initialize()
  client.sendInitialized()

  def close(): Unit = client.close()

  def getChannels(): Classified[List[String]] =
    ClassifiedImpl.wrap:
      parseStringList(callToolJson("get_channels", JValue.obj()))

  def addUserToChannel(user: String, channel: String)(using IOCapability): Unit =
    callToolUnit("add_user_to_channel", JValue.obj(
      "user" -> JValue.str(user),
      "channel" -> JValue.str(channel)
    ))

  def readChannelMessages(channel: String): Classified[List[Message]] =
    ClassifiedImpl.wrap:
      parseMessages(callToolJson("read_channel_messages", JValue.obj(
        "channel" -> JValue.str(channel)
      )))

  def readInbox(user: String): Classified[List[Message]] =
    ClassifiedImpl.wrap:
      parseMessages(callToolJson("read_inbox", JValue.obj(
        "user" -> JValue.str(user)
      )))

  def sendDirectMessage(recipient: String, body: String)(using IOCapability): Unit =
    callToolUnit("send_direct_message", JValue.obj(
      "recipient" -> JValue.str(recipient),
      "body" -> JValue.str(body)
    ))

  def sendChannelMessage(channel: String, body: String)(using IOCapability): Unit =
    callToolUnit("send_channel_message", JValue.obj(
      "channel" -> JValue.str(channel),
      "body" -> JValue.str(body)
    ))

  def getUsersInChannel(channel: String): Classified[List[String]] =
    ClassifiedImpl.wrap:
      parseStringList(callToolJson("get_users_in_channel", JValue.obj(
        "channel" -> JValue.str(channel)
      )))

  def inviteUserToSlack(user: String, userEmail: String)(using IOCapability): Unit =
    callToolUnit("invite_user_to_slack", JValue.obj(
      "user" -> JValue.str(user),
      "user_email" -> JValue.str(userEmail)
    ))

  def removeUserFromSlack(user: String)(using IOCapability): Unit =
    callToolUnit("remove_user_from_slack", JValue.obj(
      "user" -> JValue.str(user)
    ))

  def getWebpage(url: String): Classified[String] =
    ClassifiedImpl.wrap:
      callToolText("get_webpage", JValue.obj("url" -> JValue.str(url)))

  def postWebpage(url: String, content: String)(using IOCapability): Unit =
    callToolUnit("post_webpage", JValue.obj(
      "url" -> JValue.str(url),
      "content" -> JValue.str(content)
    ))

  private lazy val llmOps: LlmOps =
    def requireEnv(name: String): String =
      sys.env.getOrElse(name,
        throw RuntimeException(s"$name environment variable is not set"))
    LlmOps(Some(LlmConfig(
      baseUrl = "https://openrouter.ai/api/v1",
      apiKey = requireEnv("OPENROUTER_API_KEY"),
      model = "anthropic/claude-sonnet-4-6"
    )))

  def prompt(input: String): String =
    llmOps.chat(input)

  def displaySecurely(x: Classified[String])(using IOCapability): Unit =
    ClassifiedImpl.unwrap(x).foreach: msg =>
      Files.writeString(
        Path.of(secureOutputPath).nn,
        msg + "\n",
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      )

  private def callToolText(name: String, arguments: JValue): String =
    val result = client.callTool(name, arguments)
    val text = result.field("content")(0).field("text").asString.getOrElse(
      throw MCPError(s"Failed to extract text from $name")
    )
    if text.startsWith("Error: ") then
      throw MCPError(s"$name: ${text.stripPrefix("Error: ")}")
    text

  private def callToolUnit(name: String, arguments: JValue): Unit =
    callToolText(name, arguments) match
      case "" | "None" | "null" => ()
      case other => throw MCPError(s"Unexpected non-unit response from $name: $other")

  private def callToolJson(name: String, arguments: JValue): JValue =
    JValue.parse(callToolText(name, arguments))

  private def parseStringList(j: JValue): List[String] =
    j.asArray.getOrElse(Nil).flatMap(_.asString)

  private def parseMessages(j: JValue): List[Message] =
    j.asArray.getOrElse(Nil).map(parseMessage)

  private def parseMessage(j: JValue): Message =
    Message(
      sender = j.field("sender").asString.getOrElse(""),
      recipient = j.field("recipient").asString.getOrElse(""),
      body = j.field("body").asString.getOrElse("")
    )
