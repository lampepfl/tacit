package tacit.library.workspace

import language.experimental.captureChecking

import tacit.library.{Classified, ClassifiedImpl, LlmConfig, LlmOps}
import tacit.library.mcp.{JValue, MCPClient, MCPError}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, Path, StandardOpenOption}

class WorkspaceImpl(endpoint: String, secureOutputPath: String) extends WorkspaceService, AutoCloseable:
  private val client = MCPClient(endpoint)

  client.initialize()
  client.sendInitialized()

  def close(): Unit = client.close()

  // ── Email reads ────────────────────────────────────────────────

  def getUnreadEmails(): Classified[List[Email]] =
    ClassifiedImpl.wrap:
      callToolJson("get_unread_emails", JValue.obj())
        .asArray.getOrElse(Nil).map(parseEmail)

  def getSentEmails(): Classified[List[Email]] =
    ClassifiedImpl.wrap:
      callToolJson("get_sent_emails", JValue.obj())
        .asArray.getOrElse(Nil).map(parseEmail)

  def getReceivedEmails(): Classified[List[Email]] =
    ClassifiedImpl.wrap:
      callToolJson("get_received_emails", JValue.obj())
        .asArray.getOrElse(Nil).map(parseEmail)

  def getDraftEmails(): Classified[List[Email]] =
    ClassifiedImpl.wrap:
      callToolJson("get_draft_emails", JValue.obj())
        .asArray.getOrElse(Nil).map(parseEmail)

  def searchEmails(query: String, sender: Option[String] = None): Classified[List[Email]] =
    ClassifiedImpl.wrap:
      val args = JValue.obj("query" -> JValue.str(query)).merge(
        JValue.objOpt("sender" -> sender.map(JValue.str))
      )
      callToolJson("search_emails", args)
        .asArray.getOrElse(Nil).map(parseEmail)

  def searchContactsByName(query: String): Classified[List[EmailContact]] =
    ClassifiedImpl.wrap:
      callToolJson("search_contacts_by_name", JValue.obj("query" -> JValue.str(query)))
        .asArray.getOrElse(Nil).map(parseContact)

  def searchContactsByEmail(query: String): Classified[List[EmailContact]] =
    ClassifiedImpl.wrap:
      callToolJson("search_contacts_by_email", JValue.obj("query" -> JValue.str(query)))
        .asArray.getOrElse(Nil).map(parseContact)

  // ── Email mutations ────────────────────────────────────────────

  def sendEmail(
      recipients: List[String],
      subject: String,
      body: String,
      cc: Option[List[String]] = None,
      bcc: Option[List[String]] = None
  ): Email =
    // attachments intentionally not exposed in the initial facade — add if a use case emerges
    val base = JValue.obj(
      "recipients" -> JValue.arr(recipients.map(JValue.str)*),
      "subject" -> JValue.str(subject),
      "body" -> JValue.str(body)
    )
    val opts = JValue.objOpt(
      "cc" -> cc.map(xs => JValue.arr(xs.map(JValue.str)*)),
      "bcc" -> bcc.map(xs => JValue.arr(xs.map(JValue.str)*))
    )
    parseEmail(callToolJson("send_email", base.merge(opts)))

  def deleteEmail(emailId: String): String =
    callToolText("delete_email", JValue.obj("email_id" -> JValue.str(emailId)))

  // ── Calendar ───────────────────────────────────────────────────

  def getCurrentDay(): String =
    callToolText("get_current_day", JValue.obj())

  def searchCalendarEvents(query: String, date: Option[String] = None): Classified[List[CalendarEvent]] =
    ClassifiedImpl.wrap:
      val args = JValue.obj("query" -> JValue.str(query)).merge(
        JValue.objOpt("date" -> date.map(JValue.str))
      )
      callToolJson("search_calendar_events", args)
        .asArray.getOrElse(Nil).map(parseEvent)

  def getDayCalendarEvents(day: String): Classified[List[CalendarEvent]] =
    ClassifiedImpl.wrap:
      callToolJson("get_day_calendar_events", JValue.obj("day" -> JValue.str(day)))
        .asArray.getOrElse(Nil).map(parseEvent)

  def createCalendarEvent(
      title: String,
      startTime: String,
      endTime: String,
      description: String = "",
      participants: Option[List[String]] = None,
      location: Option[String] = None
  ): CalendarEvent =
    val base = JValue.obj(
      "title" -> JValue.str(title),
      "start_time" -> JValue.str(startTime),
      "end_time" -> JValue.str(endTime),
      "description" -> JValue.str(description)
    )
    val opts = JValue.objOpt(
      "participants" -> participants.map(xs => JValue.arr(xs.map(JValue.str)*)),
      "location" -> location.map(JValue.str)
    )
    parseEvent(callToolJson("create_calendar_event", base.merge(opts)))

  def cancelCalendarEvent(eventId: String): String =
    callToolText("cancel_calendar_event", JValue.obj("event_id" -> JValue.str(eventId)))

  def rescheduleCalendarEvent(
      eventId: String,
      newStartTime: String,
      newEndTime: Option[String] = None
  ): CalendarEvent =
    val base = JValue.obj(
      "event_id" -> JValue.str(eventId),
      "new_start_time" -> JValue.str(newStartTime)
    )
    val opts = JValue.objOpt("new_end_time" -> newEndTime.map(JValue.str))
    parseEvent(callToolJson("reschedule_calendar_event", base.merge(opts)))

  def addCalendarEventParticipants(eventId: String, participants: List[String]): CalendarEvent =
    parseEvent(callToolJson("add_calendar_event_participants", JValue.obj(
      "event_id" -> JValue.str(eventId),
      "participants" -> JValue.arr(participants.map(JValue.str)*)
    )))

  // ── Drive ──────────────────────────────────────────────────────

  def listFiles(): Classified[List[CloudDriveFile]] =
    ClassifiedImpl.wrap:
      callToolJson("list_files", JValue.obj())
        .asArray.getOrElse(Nil).map(parseFile)

  def searchFilesByFilename(filename: String): Classified[List[CloudDriveFile]] =
    ClassifiedImpl.wrap:
      callToolJson("search_files_by_filename", JValue.obj("filename" -> JValue.str(filename)))
        .asArray.getOrElse(Nil).map(parseFile)

  def searchFiles(query: String): Classified[List[CloudDriveFile]] =
    ClassifiedImpl.wrap:
      callToolJson("search_files", JValue.obj("query" -> JValue.str(query)))
        .asArray.getOrElse(Nil).map(parseFile)

  def getFileById(fileId: String): Classified[CloudDriveFile] =
    ClassifiedImpl.wrap:
      parseFile(callToolJson("get_file_by_id", JValue.obj("file_id" -> JValue.str(fileId))))

  def createFile(filename: String, content: String): CloudDriveFile =
    parseFile(callToolJson("create_file", JValue.obj(
      "filename" -> JValue.str(filename),
      "content" -> JValue.str(content)
    )))

  def deleteFile(fileId: String): CloudDriveFile =
    parseFile(callToolJson("delete_file", JValue.obj("file_id" -> JValue.str(fileId))))

  def appendToFile(fileId: String, content: String): CloudDriveFile =
    parseFile(callToolJson("append_to_file", JValue.obj(
      "file_id" -> JValue.str(fileId),
      "content" -> JValue.str(content)
    )))

  def shareFile(fileId: String, email: String, permission: SharingPermission): CloudDriveFile =
    parseFile(callToolJson("share_file", JValue.obj(
      "file_id" -> JValue.str(fileId),
      "email" -> JValue.str(email),
      "permission" -> JValue.str(sharingPermissionToWire(permission))
    )))

  // ── LLM ────────────────────────────────────────────────────────

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

  // ── Secure output ──────────────────────────────────────────────

  def displaySecurely(x: Classified[String]): Unit =
    ClassifiedImpl.unwrap(x).foreach: msg =>
      Files.writeString(
        Path.of(secureOutputPath).nn,
        msg + "\n",
        StandardCharsets.UTF_8,
        StandardOpenOption.CREATE,
        StandardOpenOption.APPEND
      )

  // ── Internals ──────────────────────────────────────────────────

  private def callToolText(name: String, arguments: JValue): String =
    val result = client.callTool(name, arguments)
    val text = result.field("content")(0).field("text").asString.getOrElse(
      throw MCPError(s"Failed to extract text from $name")
    )
    if text.startsWith("Error: ") then
      throw MCPError(s"$name: ${text.stripPrefix("Error: ")}")
    text

  private def callToolJson(name: String, arguments: JValue): JValue =
    JValue.parse(callToolText(name, arguments))

  private def parseEmail(j: JValue): Email =
    Email(
      id = j.field("id_").asString.getOrElse(""),
      sender = j.field("sender").asString.getOrElse(""),
      recipients = j.field("recipients").asArray.getOrElse(Nil).flatMap(_.asString),
      cc = j.field("cc").asArray.getOrElse(Nil).flatMap(_.asString),
      bcc = j.field("bcc").asArray.getOrElse(Nil).flatMap(_.asString),
      subject = j.field("subject").asString.getOrElse(""),
      body = j.field("body").asString.getOrElse(""),
      status = parseEmailStatus(j.field("status").asString.getOrElse("received")),
      read = j.field("read").asBool.getOrElse(false),
      timestamp = j.field("timestamp").asString.getOrElse(""),
      attachments = j.field("attachments").asArray.getOrElse(Nil).map(parseAttachment)
    )

  private def parseAttachment(j: JValue): Attachment =
    j.asString match
      case Some(fileId) => Attachment.FileRef(fileId)
      case None => Attachment.EventRef(parseEvent(j))

  private def parseContact(j: JValue): EmailContact =
    EmailContact(
      email = j.field("email").asString.getOrElse(""),
      name = j.field("name").asString.getOrElse("")
    )

  private def parseEvent(j: JValue): CalendarEvent =
    CalendarEvent(
      id = j.field("id_").asString.getOrElse(""),
      title = j.field("title").asString.getOrElse(""),
      description = j.field("description").asString.getOrElse(""),
      startTime = j.field("start_time").asString.getOrElse(""),
      endTime = j.field("end_time").asString.getOrElse(""),
      location = j.field("location").asString,
      participants = j.field("participants").asArray.getOrElse(Nil).flatMap(_.asString),
      allDay = j.field("all_day").asBool.getOrElse(false),
      status = parseEventStatus(j.field("status").asString.getOrElse("confirmed"))
    )

  private def parseFile(j: JValue): CloudDriveFile =
    CloudDriveFile(
      id = j.field("id_").asString.getOrElse(""),
      filename = j.field("filename").asString.getOrElse(""),
      content = j.field("content").asString.getOrElse(""),
      owner = j.field("owner").asString.getOrElse(""),
      lastModified = j.field("last_modified").asString.getOrElse(""),
      sharedWith = parseSharedWith(j.field("shared_with")),
      size = j.field("size").asInt.getOrElse(0)
    )

  private def parseSharedWith(j: JValue): Map[String, SharingPermission] =
    j.asObject.getOrElse(Nil).iterator.map { (k, v) =>
      k -> parseSharingPermission(v.asString.getOrElse(""))
    }.toMap

  private def parseEmailStatus(s: String): EmailStatus = s match
    case "sent" => EmailStatus.Sent
    case "received" => EmailStatus.Received
    case "draft" => EmailStatus.Draft
    case other => throw MCPError(s"Unknown EmailStatus: $other")

  private def parseEventStatus(s: String): EventStatus = s match
    case "confirmed" => EventStatus.Confirmed
    case "canceled" => EventStatus.Canceled
    case other => throw MCPError(s"Unknown EventStatus: $other")

  private def parseSharingPermission(s: String): SharingPermission = s match
    case "r" => SharingPermission.Read
    case "rw" => SharingPermission.ReadWrite
    case other => throw MCPError(s"Unknown SharingPermission: $other")

  private def sharingPermissionToWire(p: SharingPermission): String = p match
    case SharingPermission.Read => "r"
    case SharingPermission.ReadWrite => "rw"
