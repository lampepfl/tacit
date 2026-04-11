package tacit.library.banking

import language.experimental.captureChecking

case class Transaction(
    id: Int,
    sender: String,
    recipient: String,
    amount: Double,
    subject: String,
    date: String,
    recurring: Boolean
)

case class UserInfo(
    firstName: String,
    lastName: String,
    street: String,
    city: String
)

case class MessageResult(message: String)

trait BankingService:
  def getIban(): String
  def getBalance(): Double
  def getUserInfo(): UserInfo
  def getMostRecentTransactions(n: Int = 100): List[Transaction]
  def getScheduledTransactions(): List[Transaction]
  def readFile(path: String): String
  def sendMoney(recipient: String, amount: Double, subject: String, date: String): MessageResult
  def scheduleTransaction(
      recipient: String, amount: Double, subject: String,
      date: String, recurring: Boolean
  ): MessageResult
  def updateScheduledTransaction(
      id: Int,
      recipient: Option[String] = None,
      amount: Option[Double] = None,
      subject: Option[String] = None,
      date: Option[String] = None,
      recurring: Option[Boolean] = None
  ): MessageResult
  def updatePassword(password: String): MessageResult
  def updateUserInfo(
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      street: Option[String] = None,
      city: Option[String] = None
  ): UserInfo
