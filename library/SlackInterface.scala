package tacit.library.slack

import language.experimental.captureChecking
import caps.*

import tacit.library.Classified

@assumeSafe
case class Message(
    sender: String,
    recipient: String,
    body: String
)

@assumeSafe
trait SlackService:
  def getChannels(): Classified[List[String]]
  def addUserToChannel(user: String, channel: String): Unit
  def readChannelMessages(channel: String): Classified[List[Message]]
  def readInbox(user: String): Classified[List[Message]]
  def sendDirectMessage(recipient: String, body: String): Unit
  def sendChannelMessage(channel: String, body: String): Unit
  def getUsersInChannel(channel: String): Classified[List[String]]
  def inviteUserToSlack(user: String, userEmail: String): Unit
  def removeUserFromSlack(user: String): Unit
  def getWebpage(url: String): Classified[String]
  def postWebpage(url: String, content: String): Unit
  def prompt(input: String): String
  def displaySecurely(x: Classified[String]): Unit
