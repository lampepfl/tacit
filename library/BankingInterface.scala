package tacit.library.banking

import language.experimental.captureChecking
import caps.*

import dotty.tools.repl.eval.{Eval, evalLike}

import tacit.library.Classified

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

  // LLM + secure output

  /** Ask the configured trusted LLM to fill the call-site placeholder with a Scala
   *  expression of type `T`, then compile and run it under the live REPL.
   *  The synthetic parameters (`bindings`, `expectedType`, `enclosingSource`)
   *  are populated by the compiler at the call site. */
  @evalLike def agent[T](
      prompt: String,
      bindings: Array[Eval.Binding] = Array.empty[Eval.Binding],
      expectedType: String = "",
      enclosingSource: String = "",
      maxAttempts: Int = 3
  ): T

  def displaySecurely(x: Classified[String]): Unit
