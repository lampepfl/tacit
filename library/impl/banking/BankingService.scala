package tacit.library.banking

import io.circe.*
import io.circe.syntax.*
import io.circe.generic.semiauto.*
import io.circe.parser.{decode, parse}

case class Transaction(
    id: Int,
    sender: String,
    recipient: String,
    amount: Double,
    subject: String,
    date: String,
    recurring: Boolean
)

object Transaction:
  given Decoder[Transaction] = deriveDecoder

case class UserInfo(
    first_name: String,
    last_name: String,
    street: String,
    city: String
)

object UserInfo:
  given Decoder[UserInfo] = deriveDecoder

case class MessageResult(message: String)

object MessageResult:
  given Decoder[MessageResult] = deriveDecoder

class BankingService(endpoint: String) extends AutoCloseable:
  private val client = MCPClient(endpoint)

  // Initialize MCP session
  client.initialize()
  client.sendInitialized()

  def close(): Unit = client.close()

  /** Read-only queries */

  def getIban(): String =
    callToolText("get_iban", Json.obj())

  def getBalance(): Double =
    callToolText("get_balance", Json.obj()).toDouble

  def getUserInfo(): UserInfo =
    callToolParsed[UserInfo]("get_user_info", Json.obj())

  def getMostRecentTransactions(n: Int = 100): List[Transaction] =
    callToolParsed[List[Transaction]]("get_most_recent_transactions",
      Json.obj("n" -> Json.fromInt(n)))

  def getScheduledTransactions(): List[Transaction] =
    callToolParsed[List[Transaction]]("get_scheduled_transactions", Json.obj())

  def readFile(path: String): String =
    callToolText("read_file", Json.obj("file_path" -> Json.fromString(path)))

  /** Mutations */

  def sendMoney(recipient: String, amount: Double, subject: String, date: String): MessageResult =
    callToolParsed[MessageResult]("send_money", Json.obj(
      "recipient" -> Json.fromString(recipient),
      "amount" -> Json.fromDoubleOrNull(amount),
      "subject" -> Json.fromString(subject),
      "date" -> Json.fromString(date)
    ))

  def scheduleTransaction(
      recipient: String, amount: Double, subject: String,
      date: String, recurring: Boolean
  ): MessageResult =
    callToolParsed[MessageResult]("schedule_transaction", Json.obj(
      "recipient" -> Json.fromString(recipient),
      "amount" -> Json.fromDoubleOrNull(amount),
      "subject" -> Json.fromString(subject),
      "date" -> Json.fromString(date),
      "recurring" -> Json.fromBoolean(recurring)
    ))

  def updateScheduledTransaction(
      id: Int,
      recipient: Option[String] = None,
      amount: Option[Double] = None,
      subject: Option[String] = None,
      date: Option[String] = None,
      recurring: Option[Boolean] = None
  ): MessageResult =
    val args = Json.obj(
      "id" -> Json.fromInt(id)
    ).deepMerge(optionalFields(
      "recipient" -> recipient.map(Json.fromString),
      "amount" -> amount.map(Json.fromDoubleOrNull),
      "subject" -> subject.map(Json.fromString),
      "date" -> date.map(Json.fromString),
      "recurring" -> recurring.map(Json.fromBoolean)
    ))
    callToolParsed[MessageResult]("update_scheduled_transaction", args)

  def updatePassword(password: String): MessageResult =
    callToolParsed[MessageResult]("update_password",
      Json.obj("password" -> Json.fromString(password)))

  def updateUserInfo(
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      street: Option[String] = None,
      city: Option[String] = None
  ): UserInfo =
    callToolParsed[UserInfo]("update_user_info", optionalFields(
      "first_name" -> firstName.map(Json.fromString),
      "last_name" -> lastName.map(Json.fromString),
      "street" -> street.map(Json.fromString),
      "city" -> city.map(Json.fromString)
    ))

  /** Internals */

  private def callToolText(name: String, arguments: Json): String =
    val result = client.callTool(name, arguments)
    result.hcursor.downField("content").downArray
      .downField("text").as[String] match
      case Right(text) => text
      case Left(err) => throw MCPError(s"Failed to extract text from $name: $err")

  private def callToolParsed[T](name: String, arguments: Json)(using decoder: Decoder[T]): T =
    val text = callToolText(name, arguments)
    val json = BankingService.pythonReprToJson(text)
    decode[T](json) match
      case Right(value) => value
      case Left(err) => throw MCPError(s"Failed to parse response from $name: $err\nRaw: $text\nConverted: $json")

  private def optionalFields(fields: (String, Option[Json])*): Json =
    Json.fromFields(fields.collect { case (k, Some(v)) => (k, v) })

object BankingService:
  /** Convert Python repr string to JSON.
   *  Handles: single quotes to double quotes, True/False/None to true/false/null */
  def pythonReprToJson(s: String): String =
    val sb = new StringBuilder
    var i = 0
    while i < s.length do
      val c = s.charAt(i)
      if c == '\'' then
        // String literal: scan until matching unescaped single quote
        sb.append('"')
        i += 1
        while i < s.length && s.charAt(i) != '\'' do
          val ch = s.charAt(i)
          if ch == '"' then sb.append("\\\"")
          else if ch == '\\' && i + 1 < s.length then
            sb.append(ch)
            i += 1
            sb.append(s.charAt(i))
          else sb.append(ch)
          i += 1
        sb.append('"')
        i += 1 // skip closing quote
      else if s.startsWith("True", i) && !isIdentContinue(s, i + 4) then
        sb.append("true")
        i += 4
      else if s.startsWith("False", i) && !isIdentContinue(s, i + 5) then
        sb.append("false")
        i += 5
      else if s.startsWith("None", i) && !isIdentContinue(s, i + 4) then
        sb.append("null")
        i += 4
      else
        sb.append(c)
        i += 1
    sb.toString

  private def isIdentContinue(s: String, pos: Int): Boolean =
    pos < s.length && s.charAt(pos).isLetterOrDigit
