package tacit.library.banking

import language.experimental.captureChecking
import caps.*

import tacit.library.{Classified, IOCapability}

@assumeSafe
case class Transaction(
    id: Int,
    sender: String,
    recipient: String,
    amount: Double,
    subject: String,
    date: String,
    recurring: Boolean
)

@assumeSafe
case class UserInfo(
    firstName: String,
    lastName: String,
    street: String,
    city: String
)

@assumeSafe
case class MessageResult(message: String)

@assumeSafe
trait BankingService:
  def getIban(): String
  def getBalance(): Double
  def getUserInfo(): UserInfo
  def getMostRecentTransactions(n: Int = 100): Classified[List[Transaction]]
  def getScheduledTransactions(): Classified[List[Transaction]]
  def readFile(path: String): Classified[String]
  def sendMoney(recipient: String, amount: Double, subject: String, date: String)(using IOCapability): MessageResult
  def scheduleTransaction(
      recipient: String, amount: Double, subject: String,
      date: String, recurring: Boolean
  )(using IOCapability): MessageResult
  def updateScheduledTransaction(
      id: Int,
      recipient: Option[String] = None,
      amount: Option[Double] = None,
      subject: Option[String] = None,
      date: Option[String] = None,
      recurring: Option[Boolean] = None
  )(using IOCapability): MessageResult
  def updatePassword(password: String)(using IOCapability): MessageResult
  def updateUserInfo(
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      street: Option[String] = None,
      city: Option[String] = None
  )(using IOCapability): UserInfo
  def prompt(input: String): String
  def displaySecurely(x: Classified[String])(using IOCapability): Unit
