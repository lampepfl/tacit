package tacit.library.banking

import language.experimental.captureChecking

import tacit.library.{Classified, ClassifiedImpl, LlmConfig, LlmOps}

class BankingImpl(endpoint: String) extends BankingService, AutoCloseable:
  private val client = MCPClient(endpoint)

  client.initialize()
  client.sendInitialized()

  def close(): Unit = client.close()

  /** Read-only queries */

  def getIban(): String =
    callToolText("get_iban", JValue.obj())

  def getBalance(): Double =
    callToolText("get_balance", JValue.obj()).toDouble

  def getUserInfo(): UserInfo =
    parseUserInfo(callToolParsed("get_user_info", JValue.obj()))

  def getMostRecentTransactions(n: Int = 100): Classified[List[Transaction]] =
    ClassifiedImpl.wrap:
      callToolParsed("get_most_recent_transactions", JValue.obj("n" -> JValue.num(n)))
        .asArray.getOrElse(Nil).map(parseTransaction)

  def getScheduledTransactions(): Classified[List[Transaction]] =
    ClassifiedImpl.wrap:
      callToolParsed("get_scheduled_transactions", JValue.obj())
        .asArray.getOrElse(Nil).map(parseTransaction)

  def readFile(path: String): Classified[String] =
    ClassifiedImpl.wrap:
      callToolText("read_file", JValue.obj("file_path" -> JValue.str(path)))

  /** Mutations */

  def sendMoney(recipient: String, amount: Double, subject: String, date: String): MessageResult =
    parseMessage(callToolParsed("send_money", JValue.obj(
      "recipient" -> JValue.str(recipient),
      "amount" -> JValue.num(amount),
      "subject" -> JValue.str(subject),
      "date" -> JValue.str(date)
    )))

  def scheduleTransaction(
      recipient: String, amount: Double, subject: String,
      date: String, recurring: Boolean
  ): MessageResult =
    parseMessage(callToolParsed("schedule_transaction", JValue.obj(
      "recipient" -> JValue.str(recipient),
      "amount" -> JValue.num(amount),
      "subject" -> JValue.str(subject),
      "date" -> JValue.str(date),
      "recurring" -> JValue.bool(recurring)
    )))

  def updateScheduledTransaction(
      id: Int,
      recipient: Option[String] = None,
      amount: Option[Double] = None,
      subject: Option[String] = None,
      date: Option[String] = None,
      recurring: Option[Boolean] = None
  ): MessageResult =
    val base = JValue.obj("id" -> JValue.num(id))
    val opts = JValue.objOpt(
      "recipient" -> recipient.map(JValue.str),
      "amount" -> amount.map(JValue.num(_)),
      "subject" -> subject.map(JValue.str),
      "date" -> date.map(JValue.str),
      "recurring" -> recurring.map(JValue.bool)
    )
    parseMessage(callToolParsed("update_scheduled_transaction", base.merge(opts)))

  def updatePassword(password: String): MessageResult =
    parseMessage(callToolParsed("update_password",
      JValue.obj("password" -> JValue.str(password))))

  def updateUserInfo(
      firstName: Option[String] = None,
      lastName: Option[String] = None,
      street: Option[String] = None,
      city: Option[String] = None
  ): UserInfo =
    parseUserInfo(callToolParsed("update_user_info", JValue.objOpt(
      "first_name" -> firstName.map(JValue.str),
      "last_name" -> lastName.map(JValue.str),
      "street" -> street.map(JValue.str),
      "city" -> city.map(JValue.str)
    )))

  /** LLM */

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

  /** Internals */

  private def callToolText(name: String, arguments: JValue): String =
    val result = client.callTool(name, arguments)
    result.field("content")(0).field("text").asString.getOrElse(
      throw MCPError(s"Failed to extract text from $name")
    )

  private def callToolParsed(name: String, arguments: JValue): JValue =
    val text = callToolText(name, arguments)
    val json = BankingImpl.pythonReprToJson(text)
    JValue.parse(json)

  private def parseTransaction(j: JValue): Transaction =
    Transaction(
      id = j.field("id").asInt.getOrElse(0),
      sender = j.field("sender").asString.getOrElse(""),
      recipient = j.field("recipient").asString.getOrElse(""),
      amount = j.field("amount").asDouble.getOrElse(0.0),
      subject = j.field("subject").asString.getOrElse(""),
      date = j.field("date").asString.getOrElse(""),
      recurring = j.field("recurring").asBool.getOrElse(false)
    )

  private def parseUserInfo(j: JValue): UserInfo =
    UserInfo(
      firstName = j.field("first_name").asString.getOrElse(""),
      lastName = j.field("last_name").asString.getOrElse(""),
      street = j.field("street").asString.getOrElse(""),
      city = j.field("city").asString.getOrElse("")
    )

  private def parseMessage(j: JValue): MessageResult =
    MessageResult(message = j.field("message").asString.getOrElse(""))

object BankingImpl:
  /** Convert Python repr string to JSON.
   *  Handles: single quotes to double quotes, True/False/None to true/false/null */
  def pythonReprToJson(s: String): String =
    val sb = new StringBuilder
    var i = 0
    while i < s.length do
      val c = s.charAt(i)
      if c == '\'' then
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
        i += 1
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
