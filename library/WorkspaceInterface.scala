package tacit.library.workspace

import language.experimental.captureChecking

import tacit.library.Classified

enum EmailStatus:
  case Sent, Received, Draft

enum EventStatus:
  case Confirmed, Canceled

enum SharingPermission:
  case Read, ReadWrite

enum Attachment:
  case FileRef(fileId: String)
  case EventRef(event: CalendarEvent)

case class EmailContact(email: String, name: String)

case class Email(
    id: String,
    sender: String,
    recipients: List[String],
    cc: List[String],
    bcc: List[String],
    subject: String,
    body: String,
    status: EmailStatus,
    read: Boolean,
    timestamp: String,
    attachments: List[Attachment]
)

case class CalendarEvent(
    id: String,
    title: String,
    description: String,
    startTime: String,
    endTime: String,
    location: Option[String],
    participants: List[String],
    allDay: Boolean,
    status: EventStatus
)

case class CloudDriveFile(
    id: String,
    filename: String,
    content: String,
    owner: String,
    lastModified: String,
    sharedWith: Map[String, SharingPermission],
    size: Int
)

trait WorkspaceService:
  // Email — reads (wrapped in Classified: content may come from external senders)
  def getUnreadEmails(): Classified[List[Email]]
  def getSentEmails(): Classified[List[Email]]
  def getReceivedEmails(): Classified[List[Email]]
  def getDraftEmails(): Classified[List[Email]]
  def searchEmails(query: String, sender: Option[String] = None): Classified[List[Email]]
  def searchContactsByName(query: String): Classified[List[EmailContact]]
  def searchContactsByEmail(query: String): Classified[List[EmailContact]]

  // Email — mutations
  def sendEmail(
      recipients: List[String],
      subject: String,
      body: String,
      attachments: Option[List[Attachment]] = None,
      cc: Option[List[String]] = None,
      bcc: Option[List[String]] = None
  ): Email
  def deleteEmail(emailId: String): String

  // Calendar
  def getCurrentDay(): String
  def searchCalendarEvents(query: String, date: Option[String] = None): Classified[List[CalendarEvent]]
  def getDayCalendarEvents(day: String): Classified[List[CalendarEvent]]
  def createCalendarEvent(
      title: String,
      startTime: String,
      endTime: String,
      description: String = "",
      participants: Option[List[String]] = None,
      location: Option[String] = None
  ): CalendarEvent
  def cancelCalendarEvent(eventId: String): String
  def rescheduleCalendarEvent(
      eventId: String,
      newStartTime: String,
      newEndTime: Option[String] = None
  ): CalendarEvent
  def addCalendarEventParticipants(eventId: String, participants: List[String]): CalendarEvent

  // Drive
  def listFiles(): Classified[List[CloudDriveFile]]
  def searchFilesByFilename(filename: String): Classified[List[CloudDriveFile]]
  def searchFiles(query: String): Classified[List[CloudDriveFile]]
  def getFileById(fileId: String): Classified[CloudDriveFile]
  def createFile(filename: String, content: String): CloudDriveFile
  def deleteFile(fileId: String): CloudDriveFile
  def appendToFile(fileId: String, content: String): CloudDriveFile
  def shareFile(fileId: String, email: String, permission: SharingPermission): CloudDriveFile

  // LLM + secure output
  def prompt(input: String): String
  def displaySecurely(x: Classified[String]): Unit
