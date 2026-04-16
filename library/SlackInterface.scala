package tacit.library.slack

import language.experimental.captureChecking

import tacit.library.{Classified, IOCapability}

case class Message(
    sender: String,
    recipient: String,
    body: String
)

trait SlackService:
  def getChannels(): Classified[List[String]]
  def addUserToChannel(user: String, channel: String)(using IOCapability): Unit
  def readChannelMessages(channel: String): Classified[List[Message]]
  def readInbox(user: String): Classified[List[Message]]
  def sendDirectMessage(recipient: String, body: String)(using IOCapability): Unit
  def sendChannelMessage(channel: String, body: String)(using IOCapability): Unit
  def getUsersInChannel(channel: String): Classified[List[String]]
  def inviteUserToSlack(user: String, userEmail: String)(using IOCapability): Unit
  def removeUserFromSlack(user: String)(using IOCapability): Unit
  def getWebpage(url: String): Classified[String]
  def postWebpage(url: String, content: String)(using IOCapability): Unit
  def prompt(input: String): String
  def displaySecurely(x: Classified[String])(using IOCapability): Unit
