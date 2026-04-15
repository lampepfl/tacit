package tacit.library.workspace

import language.experimental.captureChecking

import tacit.library.{Classified, ClassifiedImpl}
import tacit.library.mcp.MCPError

import java.nio.charset.StandardCharsets
import java.nio.file.Files

private var passed = 0
private var failed = 0

private def unwrap[T](c: Classified[T]): T = ClassifiedImpl.unwrap(c).get

private def assert(cond: Boolean, msg: String): Unit =
  if cond then passed += 1
  else
    failed += 1
    System.err.nn.println(s"  FAIL: $msg")

private def assertEquals[T](actual: T, expected: T, label: String): Unit =
  assert(actual == expected, s"$label: expected $expected, got $actual")

private def section(name: String): Unit =
  System.out.nn.println(s"\n--- $name ---")

private def expectError(label: String)(thunk: => Any): Unit =
  try
    val _ = thunk
    failed += 1
    System.err.nn.println(s"  FAIL: $label: expected MCPError, got success")
  catch
    case _: MCPError => passed += 1
    case e: Throwable =>
      failed += 1
      System.err.nn.println(s"  FAIL: $label: expected MCPError, got ${e.getClass.getSimpleName}: ${e.getMessage}")

@main def TestWorkspace(args: String*): Unit =
  val argList = args.toList
  val port = argList.sliding(2).collectFirst {
    case "--agentdojo-port" :: p :: Nil => p.toInt
  }.getOrElse(63353)
  val endpoint = s"http://localhost:$port/mcp"
  val out = System.out.nn
  out.println(s"=== Workspace Service Test (endpoint: $endpoint) ===")

  val secureOutputFile = Files.createTempFile("workspace-secure-", ".txt").nn
  val svc = WorkspaceImpl(endpoint, secureOutputFile.toString)

  val testTag = System.currentTimeMillis.toString
  val uniqueSubject = s"tacit-test-subject-$testTag"
  val uniqueEventTitle = s"tacit-test-event-$testTag"
  val uniqueFilename = s"tacit-test-file-$testTag.txt"
  val uniqueFileContent = s"tacit-test-content-$testTag apple"

  try
    // ── getCurrentDay ─────────────────────────────────────────────

    section("getCurrentDay")
    val today = svc.getCurrentDay()
    assert(today.matches("\\d{4}-\\d{2}-\\d{2}"), s"current day ISO format (got '$today')")
    assertEquals(today, "2024-05-15", "current day matches fixture")

    // ── Email reads ───────────────────────────────────────────────

    section("getReceivedEmails")
    val received = unwrap(svc.getReceivedEmails())
    assert(received.size >= 5, s"at least 5 received emails (got ${received.size})")
    val birthday = received.find(_.subject == "Birthday Party")
    assert(birthday.isDefined, "Birthday Party email exists")
    birthday.foreach { e =>
      assertEquals(e.sender, "lily.white@gmail.com", "Birthday Party sender")
      assertEquals(e.status, EmailStatus.Received, "Birthday Party status")
      assert(e.body.contains("John"), "Birthday Party body mentions John")
    }

    section("getSentEmails")
    val sent = unwrap(svc.getSentEmails())
    assert(sent.size >= 5, s"at least 5 sent emails (got ${sent.size})")
    val projectUpdate = sent.find(_.subject == "Project Update")
    assert(projectUpdate.isDefined, "Project Update email exists")
    projectUpdate.foreach { e =>
      assertEquals(e.sender, "emma.johnson@bluesparrowtech.com", "Project Update sender")
      assertEquals(e.status, EmailStatus.Sent, "Project Update status")
      assert(e.recipients.contains("david.smith@bluesparrowtech.com"), "Project Update recipient")
      assert(e.cc.contains("julie.williams@bluesparrowtech.com"), "Project Update cc")
    }

    section("getDraftEmails")
    val drafts = unwrap(svc.getDraftEmails())
    assert(drafts.size >= 0, "getDraftEmails returns a list")
    drafts.foreach { e =>
      assertEquals(e.status, EmailStatus.Draft, "draft has status Draft")
    }

    section("getUnreadEmails")
    // Note: get_unread_emails marks matching emails as read as a side-effect
    // before returning, so the returned Email objects already have read=true.
    // Subsequent calls therefore return an empty list.
    val unread = unwrap(svc.getUnreadEmails())
    assert(unread.size >= 0, "getUnreadEmails returns a list")
    val unreadAgain = unwrap(svc.getUnreadEmails())
    assertEquals(unreadAgain.size, 0, "second getUnreadEmails call is empty (side-effect applied)")

    // ── Contact search ────────────────────────────────────────────

    section("searchContactsByName")
    val sarahByName = unwrap(svc.searchContactsByName("Sarah"))
    assert(sarahByName.nonEmpty, "search for 'Sarah' returns at least one contact")
    assert(sarahByName.exists(_.email == "sarah.baker@gmail.com"),
      "sarah.baker@gmail.com appears in Sarah search")

    section("searchContactsByEmail")
    val davidByEmail = unwrap(svc.searchContactsByEmail("david.smith@bluesparrowtech.com"))
    assert(davidByEmail.nonEmpty, "search for david.smith returns at least one contact")
    assert(davidByEmail.exists(_.email == "david.smith@bluesparrowtech.com"),
      "david.smith email matches")

    // ── searchEmails ──────────────────────────────────────────────

    section("searchEmails")
    val birthdayHits = unwrap(svc.searchEmails("Birthday Party"))
    assert(birthdayHits.nonEmpty, "search 'Birthday Party' returns at least one email")
    assert(birthdayHits.exists(_.subject == "Birthday Party"), "result contains Birthday Party")

    val projectHitsBySender = unwrap(
      svc.searchEmails("Project", sender = Some("david.smith@bluesparrowtech.com"))
    )
    assert(projectHitsBySender.nonEmpty, "search 'Project' filtered by david.smith returns results")
    projectHitsBySender.foreach { e =>
      assertEquals(e.sender, "david.smith@bluesparrowtech.com", "filtered email sender")
    }

    // ── sendEmail round-trip ──────────────────────────────────────

    section("sendEmail")
    val sentEmail = svc.sendEmail(
      recipients = List("test.recipient@example.com"),
      subject = uniqueSubject,
      body = "Hello from the TestWorkspace harness."
    )
    assertEquals(sentEmail.subject, uniqueSubject, "send returned subject")
    assertEquals(sentEmail.status, EmailStatus.Sent, "send returned status")
    assertEquals(sentEmail.sender, "emma.johnson@bluesparrowtech.com", "send returned sender")
    assert(sentEmail.recipients.contains("test.recipient@example.com"), "send returned recipient")
    assert(sentEmail.id.nonEmpty, "send returned non-empty id")

    val sentAfter = unwrap(svc.getSentEmails())
    val roundTripped = sentAfter.find(_.id == sentEmail.id)
    assert(roundTripped.isDefined, "sent email appears in getSentEmails")
    roundTripped.foreach { e =>
      assertEquals(e.subject, uniqueSubject, "round-tripped subject")
      assertEquals(e.body, "Hello from the TestWorkspace harness.", "round-tripped body")
    }

    // ── deleteEmail ───────────────────────────────────────────────

    section("deleteEmail")
    val delMsg = svc.deleteEmail(sentEmail.id)
    assert(delMsg.contains(sentEmail.id), s"delete message mentions id (got '$delMsg')")
    val sentAfterDelete = unwrap(svc.getSentEmails())
    assert(sentAfterDelete.forall(_.id != sentEmail.id), "deleted email is gone from getSentEmails")

    // ── sendEmail with attachments ───────────────────────────────

    section("sendEmail with attachments")
    val attachFileId = s"tacit-attach-file-$testTag"
    val attachEvent = CalendarEvent(
      id = s"tacit-attach-event-$testTag",
      title = s"Attached event $testTag",
      description = "inline event attached to an email",
      startTime = "2024-05-30T09:00:00",
      endTime = "2024-05-30T10:00:00",
      location = Some("Somewhere"),
      participants = List("alice@example.com"),
      allDay = false,
      status = EventStatus.Confirmed
    )
    val withAttachments = svc.sendEmail(
      recipients = List("test.recipient@example.com"),
      subject = s"$uniqueSubject-attach",
      body = "body with attachments",
      attachments = Some(List(
        Attachment.FileRef(attachFileId),
        Attachment.EventRef(attachEvent)
      ))
    )
    assertEquals(withAttachments.attachments.size, 2, "attachments round-trip count")
    assert(
      withAttachments.attachments.exists {
        case Attachment.FileRef(id) => id == attachFileId
        case _ => false
      },
      "attachments include FileRef with round-tripped id"
    )
    assert(
      withAttachments.attachments.exists {
        case Attachment.EventRef(e) =>
          e.title == attachEvent.title && e.description == attachEvent.description
        case _ => false
      },
      "attachments include EventRef with round-tripped title + description"
    )
    svc.deleteEmail(withAttachments.id)

    // ── Calendar reads ────────────────────────────────────────────

    section("getDayCalendarEvents")
    val day0515 = unwrap(svc.getDayCalendarEvents("2024-05-15"))
    assert(day0515.size >= 3, s"at least 3 events on 2024-05-15 (got ${day0515.size})")
    val teamSync = day0515.find(_.title == "Team Sync")
    assert(teamSync.isDefined, "Team Sync event exists on 2024-05-15")
    teamSync.foreach { e =>
      assert(e.startTime.startsWith("2024-05-15T10:00"), s"Team Sync start time (got ${e.startTime})")
      assert(e.endTime.startsWith("2024-05-15T11:00"), s"Team Sync end time (got ${e.endTime})")
      assertEquals(e.location, Some("Conference Room B"), "Team Sync location")
      assertEquals(e.status, EventStatus.Confirmed, "Team Sync status")
      assert(e.participants.contains("michael.smith@bluesparrowtech.com"),
        "Team Sync participants include michael")
    }

    section("searchCalendarEvents")
    val teamSyncHits = unwrap(svc.searchCalendarEvents("Team Sync"))
    assert(teamSyncHits.exists(_.title == "Team Sync"), "searchCalendarEvents finds Team Sync")

    val dentistHits = unwrap(svc.searchCalendarEvents("Dentist", date = Some("2024-05-18")))
    assert(dentistHits.nonEmpty, "searchCalendarEvents finds Dentist Appointment on 2024-05-18")

    // ── Calendar create → reschedule → addParticipants → cancel ──

    section("createCalendarEvent")
    val newEvent = svc.createCalendarEvent(
      title = uniqueEventTitle,
      startTime = "2024-05-25 10:00",
      endTime = "2024-05-25 11:00",
      description = "Unit-test event",
      participants = Some(List("alice@example.com", "bob@example.com")),
      location = Some("Test Room")
    )
    assertEquals(newEvent.title, uniqueEventTitle, "new event title")
    assertEquals(newEvent.location, Some("Test Room"), "new event location")
    assertEquals(newEvent.status, EventStatus.Confirmed, "new event status")
    assert(newEvent.participants.contains("alice@example.com"), "new event has alice")
    assert(newEvent.participants.contains("bob@example.com"), "new event has bob")
    // The calendar owner is always added by the server
    assert(newEvent.participants.contains("emma.johnson@bluesparrowtech.com"),
      "new event includes owner")
    assert(newEvent.id.nonEmpty, "new event id non-empty")

    val dayAfterCreate = unwrap(svc.getDayCalendarEvents("2024-05-25"))
    assert(dayAfterCreate.exists(_.id == newEvent.id), "new event appears in day query")

    section("rescheduleCalendarEvent")
    val rescheduled = svc.rescheduleCalendarEvent(
      eventId = newEvent.id,
      newStartTime = "2024-05-25 14:00",
      newEndTime = Some("2024-05-25 15:30")
    )
    assertEquals(rescheduled.id, newEvent.id, "rescheduled id preserved")
    assert(rescheduled.startTime.startsWith("2024-05-25T14:00"),
      s"rescheduled start (got ${rescheduled.startTime})")
    assert(rescheduled.endTime.startsWith("2024-05-25T15:30"),
      s"rescheduled end (got ${rescheduled.endTime})")

    section("addCalendarEventParticipants")
    val withCarol = svc.addCalendarEventParticipants(
      eventId = newEvent.id,
      participants = List("carol@example.com")
    )
    assert(withCarol.participants.contains("carol@example.com"), "carol added")
    assert(withCarol.participants.contains("alice@example.com"), "alice preserved")
    assert(withCarol.participants.contains("bob@example.com"), "bob preserved")

    section("cancelCalendarEvent")
    val cancelMsg = svc.cancelCalendarEvent(newEvent.id)
    assert(cancelMsg.contains(newEvent.id), s"cancel message mentions id (got '$cancelMsg')")

    // ── Drive reads ───────────────────────────────────────────────

    section("listFiles")
    val allFiles = unwrap(svc.listFiles())
    assert(allFiles.size >= 3, s"at least 3 files (got ${allFiles.size})")
    val feedback = allFiles.find(_.filename == "feedback.xlsx")
    assert(feedback.isDefined, "feedback.xlsx exists")
    feedback.foreach { f =>
      assertEquals(f.owner, "emma.johnson@bluesparrowtech.com", "feedback owner")
      assertEquals(
        f.sharedWith.get("alex.martin@bluesparrowtech.com"),
        Some(SharingPermission.Read),
        "feedback shared with alex (r)"
      )
      assertEquals(
        f.sharedWith.get("linda.jameson@bluesparrowtech.com"),
        Some(SharingPermission.ReadWrite),
        "feedback shared with linda (rw)"
      )
      assert(f.content.contains("Olivia Smith"), "feedback content includes Olivia Smith")
    }

    section("searchFilesByFilename")
    val feedbackByName = unwrap(svc.searchFilesByFilename("feedback"))
    assert(feedbackByName.exists(_.filename == "feedback.xlsx"), "search by filename finds feedback.xlsx")

    section("searchFiles")
    val phoenixMatches = unwrap(svc.searchFiles("project"))
    assert(phoenixMatches.nonEmpty, "search content 'project' returns at least one file")

    section("getFileById")
    feedback.foreach { f =>
      val fetched = unwrap(svc.getFileById(f.id))
      assertEquals(fetched.id, f.id, "getFileById id")
      assertEquals(fetched.filename, "feedback.xlsx", "getFileById filename")
    }

    // ── Drive create → getById → append → share → delete ────────

    section("createFile")
    val createdFile = svc.createFile(uniqueFilename, uniqueFileContent)
    assertEquals(createdFile.filename, uniqueFilename, "created filename")
    assertEquals(createdFile.content, uniqueFileContent, "created content")
    assertEquals(createdFile.owner, "emma.johnson@bluesparrowtech.com", "created owner")
    assert(createdFile.id.nonEmpty, "created id non-empty")
    val initialSize = createdFile.size
    assert(initialSize > 0, "created size > 0")

    section("appendToFile")
    val appended = svc.appendToFile(createdFile.id, " banana")
    assert(appended.content.contains("apple"), "appended content keeps original 'apple'")
    assert(appended.content.contains("banana"), "appended content has new 'banana'")
    assert(appended.size > initialSize, "size grew after append")

    section("shareFile")
    val shared = svc.shareFile(createdFile.id, "recipient@example.com", SharingPermission.ReadWrite)
    assertEquals(
      shared.sharedWith.get("recipient@example.com"),
      Some(SharingPermission.ReadWrite),
      "shared with recipient (rw)"
    )

    section("deleteFile")
    val deleted = svc.deleteFile(createdFile.id)
    assertEquals(deleted.id, createdFile.id, "deleted file id")
    val afterDelete = unwrap(svc.listFiles())
    assert(afterDelete.forall(_.id != createdFile.id), "deleted file gone from listFiles")

    // ═══════════════════════════════════════════════════════════════
    // Corner cases & error-path verification
    // ═══════════════════════════════════════════════════════════════

    // ── Email error paths ────────────────────────────────────────

    section("Email error paths")
    expectError("searchEmails with no match raises MCPError") {
      unwrap(svc.searchEmails(s"no-match-query-$testTag-xyzabc123"))
    }
    expectError("searchContactsByName with no match raises MCPError") {
      unwrap(svc.searchContactsByName(s"ghost-name-$testTag-xyz"))
    }
    expectError("searchContactsByEmail with no match raises MCPError") {
      unwrap(svc.searchContactsByEmail(s"nobody-$testTag@nonexistent.invalid"))
    }
    expectError("deleteEmail with bogus id raises MCPError") {
      svc.deleteEmail(s"bogus-email-id-$testTag")
    }

    // ── sendEmail cc/bcc variations ──────────────────────────────

    section("sendEmail cc only")
    val sentCcOnly = svc.sendEmail(
      recipients = List("to1@example.com"),
      subject = s"$uniqueSubject-cc-only",
      body = "cc only",
      cc = Some(List("cc1@example.com", "cc2@example.com"))
    )
    assertEquals(sentCcOnly.cc.size, 2, "cc list size")
    assert(sentCcOnly.cc.contains("cc1@example.com"), "cc contains cc1")
    assert(sentCcOnly.cc.contains("cc2@example.com"), "cc contains cc2")
    assertEquals(sentCcOnly.bcc.size, 0, "bcc empty when not passed")
    svc.deleteEmail(sentCcOnly.id)

    section("sendEmail bcc only")
    val sentBccOnly = svc.sendEmail(
      recipients = List("to1@example.com"),
      subject = s"$uniqueSubject-bcc-only",
      body = "bcc only",
      bcc = Some(List("bcc1@example.com"))
    )
    assertEquals(sentBccOnly.bcc.size, 1, "bcc list size")
    assert(sentBccOnly.bcc.contains("bcc1@example.com"), "bcc contains bcc1")
    assertEquals(sentBccOnly.cc.size, 0, "cc empty when not passed")
    svc.deleteEmail(sentBccOnly.id)

    section("sendEmail mixed cc + bcc + attachments")
    val sentMixed = svc.sendEmail(
      recipients = List("to1@example.com", "to2@example.com"),
      subject = s"$uniqueSubject-mixed",
      body = "cc, bcc, and attachments together",
      attachments = Some(List(
        Attachment.FileRef(s"fileref-a-$testTag"),
        Attachment.FileRef(s"fileref-b-$testTag")
      )),
      cc = Some(List("cc@example.com")),
      bcc = Some(List("bcc@example.com"))
    )
    assertEquals(sentMixed.recipients.size, 2, "recipients count = 2")
    assertEquals(sentMixed.cc.size, 1, "cc count = 1")
    assertEquals(sentMixed.bcc.size, 1, "bcc count = 1")
    assertEquals(sentMixed.attachments.size, 2, "attachments count = 2")
    assert(
      sentMixed.attachments.forall {
        case Attachment.FileRef(_) => true
        case _ => false
      },
      "all attachments are FileRefs"
    )
    svc.deleteEmail(sentMixed.id)

    section("sendEmail with empty attachments list")
    val sentEmptyAttach = svc.sendEmail(
      recipients = List("to1@example.com"),
      subject = s"$uniqueSubject-empty-attach",
      body = "empty attachments list",
      attachments = Some(Nil)
    )
    assertEquals(sentEmptyAttach.attachments.size, 0, "empty attachments list persists as empty")
    svc.deleteEmail(sentEmptyAttach.id)

    section("sendEmail only event attachment (allDay, null location, Canceled)")
    val onlyEvent = CalendarEvent(
      id = s"attached-event-$testTag",
      title = s"Only Event $testTag",
      description = "single event attachment with edge-case fields",
      startTime = "2024-06-01T10:00:00",
      endTime = "2024-06-01T11:00:00",
      location = None,
      participants = List("p1@example.com", "p2@example.com"),
      allDay = true,
      status = EventStatus.Canceled
    )
    val sentOnlyEvent = svc.sendEmail(
      recipients = List("to@example.com"),
      subject = s"$uniqueSubject-only-event",
      body = "single event attachment",
      attachments = Some(List(Attachment.EventRef(onlyEvent)))
    )
    assertEquals(sentOnlyEvent.attachments.size, 1, "only-event attachment count")
    sentOnlyEvent.attachments.headOption match
      case Some(Attachment.EventRef(e)) =>
        assertEquals(e.title, onlyEvent.title, "event attachment title")
        assertEquals(e.description, onlyEvent.description, "event attachment description")
        assertEquals(e.allDay, true, "allDay=true round-trip")
        assertEquals(e.status, EventStatus.Canceled, "status=Canceled round-trip")
        assertEquals(e.location, None, "null location round-trip")
        assertEquals(e.participants.size, 2, "participants size round-trip")
        assert(e.participants.contains("p1@example.com"), "participant p1 round-trip")
      case other =>
        assert(false, s"expected EventRef, got $other")
    svc.deleteEmail(sentOnlyEvent.id)

    // ── Calendar error paths ────────────────────────────────────

    section("Calendar error paths")
    expectError("searchCalendarEvents with no match raises MCPError") {
      unwrap(svc.searchCalendarEvents(s"no-event-match-$testTag-xyz"))
    }
    expectError("cancelCalendarEvent with bogus id raises MCPError") {
      svc.cancelCalendarEvent(s"bogus-event-id-$testTag")
    }
    expectError("rescheduleCalendarEvent with bogus id raises MCPError") {
      svc.rescheduleCalendarEvent(s"bogus-event-$testTag", "2024-06-01 10:00")
    }
    expectError("addCalendarEventParticipants with bogus id raises MCPError") {
      svc.addCalendarEventParticipants(s"bogus-event-$testTag", List("x@example.com"))
    }

    // ── createCalendarEvent defaults ─────────────────────────────

    section("createCalendarEvent defaults")
    val defaultEvent = svc.createCalendarEvent(
      title = s"Default event $testTag",
      startTime = "2024-06-15 09:00",
      endTime = "2024-06-15 10:00"
    )
    assertEquals(defaultEvent.description, "", "default description is empty")
    assertEquals(defaultEvent.location, None, "default location is None")
    assertEquals(defaultEvent.allDay, false, "default allDay is false")
    assertEquals(defaultEvent.status, EventStatus.Confirmed, "default status is Confirmed")
    assertEquals(defaultEvent.participants.size, 1, "default participants = only owner")
    assertEquals(defaultEvent.participants.head, "emma.johnson@bluesparrowtech.com", "owner auto-added")
    svc.cancelCalendarEvent(defaultEvent.id)

    // ── rescheduleCalendarEvent duration preservation ───────────

    section("rescheduleCalendarEvent preserves duration")
    val durEvent = svc.createCalendarEvent(
      title = s"Duration test $testTag",
      startTime = "2024-06-20 14:00",
      endTime = "2024-06-20 16:00",
      description = "duration-preservation test"
    )
    val durRescheduled = svc.rescheduleCalendarEvent(
      eventId = durEvent.id,
      newStartTime = "2024-06-20 10:00"
    )
    assert(durRescheduled.startTime.startsWith("2024-06-20T10:00"),
      s"moved start (got ${durRescheduled.startTime})")
    assert(durRescheduled.endTime.startsWith("2024-06-20T12:00"),
      s"2h duration preserved (got ${durRescheduled.endTime})")
    svc.cancelCalendarEvent(durEvent.id)

    // ── Drive error paths ───────────────────────────────────────

    section("Drive error paths")
    expectError("searchFiles with no match raises MCPError") {
      unwrap(svc.searchFiles(s"no-file-content-match-$testTag-xyzabc"))
    }
    expectError("getFileById with bogus id raises MCPError") {
      unwrap(svc.getFileById(s"bogus-file-id-$testTag"))
    }
    expectError("deleteFile with bogus id raises MCPError") {
      svc.deleteFile(s"bogus-file-id-$testTag")
    }
    expectError("appendToFile with bogus id raises MCPError") {
      svc.appendToFile(s"bogus-file-id-$testTag", "content")
    }
    expectError("shareFile with bogus id raises MCPError") {
      svc.shareFile(s"bogus-file-id-$testTag", "x@example.com", SharingPermission.Read)
    }

    // ── searchFilesByFilename with no match returns empty list ──

    section("searchFilesByFilename no-match returns empty list")
    val noFileByName = unwrap(svc.searchFilesByFilename(s"no-file-name-$testTag-xyz"))
    assertEquals(noFileByName.size, 0, "empty list for no filename match")

    // ── Drive share variations ──────────────────────────────────

    section("Drive share variations (Read, multi-user, re-share upgrade)")
    val shareTarget = svc.createFile(
      filename = s"share-test-$testTag.txt",
      content = "share test content"
    )

    val sharedRead = svc.shareFile(shareTarget.id, "reader1@example.com", SharingPermission.Read)
    assertEquals(
      sharedRead.sharedWith.get("reader1@example.com"),
      Some(SharingPermission.Read),
      "first share is Read"
    )

    val sharedTwo = svc.shareFile(shareTarget.id, "writer@example.com", SharingPermission.ReadWrite)
    assertEquals(sharedTwo.sharedWith.size, 2, "two users in sharedWith")
    assertEquals(
      sharedTwo.sharedWith.get("reader1@example.com"),
      Some(SharingPermission.Read),
      "first user still Read"
    )
    assertEquals(
      sharedTwo.sharedWith.get("writer@example.com"),
      Some(SharingPermission.ReadWrite),
      "second user is ReadWrite"
    )

    val sharedUpgraded = svc.shareFile(shareTarget.id, "reader1@example.com", SharingPermission.ReadWrite)
    assertEquals(
      sharedUpgraded.sharedWith.get("reader1@example.com"),
      Some(SharingPermission.ReadWrite),
      "reader1 upgraded to ReadWrite"
    )
    assertEquals(sharedUpgraded.sharedWith.size, 2, "still two users after re-share")
    svc.deleteFile(shareTarget.id)

    // ── createFile edge cases ───────────────────────────────────

    section("createFile with empty content")
    val emptyFile = svc.createFile(s"empty-$testTag.txt", "")
    assertEquals(emptyFile.content, "", "empty content preserved")
    assertEquals(emptyFile.size, 0, "empty file size is 0")
    svc.deleteFile(emptyFile.id)

    // ── Classified integrity ────────────────────────────────────

    section("Classified map / flatMap / toString")
    val receivedClassified = svc.getReceivedEmails()
    // toString MUST NOT leak
    assertEquals(receivedClassified.toString, "Classified(***)", "toString masks classified")

    // map preserves size
    val subjectsClassified: Classified[List[String]] = receivedClassified.map(_.map(_.subject))
    val countClassified: Classified[Int] = subjectsClassified.map(_.size)
    val baseCount = unwrap(receivedClassified).size
    assertEquals(unwrap(countClassified), baseCount, "map chain preserves list size")

    // flatMap chains inside the wrapper
    val doubledCount: Classified[Int] = receivedClassified.flatMap { emails =>
      ClassifiedImpl.wrap(emails.size * 2)
    }
    assertEquals(unwrap(doubledCount), baseCount * 2, "flatMap chain: doubled size")

    // Deeply-nested map chain
    val deepSum: Classified[Int] = receivedClassified
      .map(_.map(_.subject.length))
      .map(_.sum)
    val manualDeepSum = unwrap(receivedClassified).map(_.subject.length).sum
    assertEquals(unwrap(deepSum), manualDeepSum, "deeply nested map chain")

    // ── displaySecurely ──────────────────────────────────────────

    section("displaySecurely")
    svc.displaySecurely(ClassifiedImpl.wrap("hello secure workspace"))
    svc.displaySecurely(ClassifiedImpl.wrap("second workspace message"))
    // Edge cases: empty, unicode (emoji + Japanese), tab
    svc.displaySecurely(ClassifiedImpl.wrap(""))
    svc.displaySecurely(ClassifiedImpl.wrap("unicode: 🎉 日本語"))
    svc.displaySecurely(ClassifiedImpl.wrap("line with\ttab char"))
    val secureContent = Files.readString(secureOutputFile, StandardCharsets.UTF_8).nn
    assert(secureContent.contains("hello secure workspace"), "secure output contains first message")
    assert(secureContent.contains("second workspace message"), "secure output contains second message")
    assert(secureContent.contains("🎉"), "unicode emoji preserved")
    assert(secureContent.contains("日本語"), "japanese text preserved")
    assert(secureContent.contains("\t"), "tab char preserved")
    // 5 messages → 5 newlines total (one per write)
    assertEquals(secureContent.count(_ == '\n'), 5, "five lines written")

    // ── Summary ──────────────────────────────────────────────────

    out.println()
    out.println(s"=== $passed passed, $failed failed ===")
    if failed > 0 then System.exit(1)
  catch
    case e: MCPError =>
      System.err.nn.println(s"\nMCP error: ${e.getMessage}")
      System.exit(1)
    case e: java.net.ConnectException =>
      System.err.nn.println(s"\nConnection refused: is the workspace MCP server running at $endpoint?")
      System.exit(1)
    case e: Exception =>
      System.err.nn.println(s"\nUnexpected error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
  finally
    svc.close()
    Files.deleteIfExists(secureOutputFile)
