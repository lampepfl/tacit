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

    // ── displaySecurely ──────────────────────────────────────────

    section("displaySecurely")
    svc.displaySecurely(ClassifiedImpl.wrap("hello secure workspace"))
    svc.displaySecurely(ClassifiedImpl.wrap("second workspace message"))
    val secureContent = Files.readString(secureOutputFile, StandardCharsets.UTF_8).nn
    assert(secureContent.contains("hello secure workspace"), "secure output contains first message")
    assert(secureContent.contains("second workspace message"), "secure output contains second message")
    assertEquals(secureContent.count(_ == '\n'), 2, "two lines written")

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
