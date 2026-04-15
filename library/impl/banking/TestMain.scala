package tacit.library.banking

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

@main def TestBanking(args: String*): Unit =
  val argList = args.toList
  val port = argList.sliding(2).collectFirst {
    case "--agentdojo-port" :: p :: Nil => p.toInt
  }.getOrElse(61954)
  val endpoint = s"http://localhost:$port/mcp"
  val out = System.out.nn
  out.println(s"=== Banking Service Test (endpoint: $endpoint) ===")

  val secureOutputFile = Files.createTempFile("banking-secure-", ".txt").nn
  val svc = BankingImpl(endpoint, secureOutputFile.toString)
  try
    // ── Initial state: verify known fixtures are present ────────

    section("getIban")
    val iban = svc.getIban()
    assertEquals(iban, "DE89370400440532013000", "IBAN")

    section("getBalance")
    val balance = svc.getBalance()
    assertEquals(balance, 1810.0, "balance")

    section("getUserInfo")
    val user = svc.getUserInfo()
    // First/last name should be stable in a fresh environment
    out.println(s"  user: ${user.firstName} ${user.lastName}, ${user.street}, ${user.city}")
    assert(user.firstName.nonEmpty, "firstName non-empty")
    assert(user.lastName.nonEmpty, "lastName non-empty")
    assert(user.street.nonEmpty, "street non-empty")
    assert(user.city.nonEmpty, "city non-empty")

    section("getMostRecentTransactions")
    val allTxns = unwrap(svc.getMostRecentTransactions(100))
    assert(allTxns.size >= 5, s"at least 5 transactions (got ${allTxns.size})")

    // Verify known fixture transactions exist
    val t1 = allTxns.find(_.id == 1)
    assert(t1.isDefined, "transaction 1 exists")
    t1.foreach { t =>
      assertEquals(t.sender, "me", "t1.sender")
      assertEquals(t.recipient, "CH9300762011623852957", "t1.recipient")
      assertEquals(t.amount, 100.0, "t1.amount")
      assertEquals(t.subject, "Pizza party", "t1.subject")
      assertEquals(t.date, "2022-01-01", "t1.date")
      assertEquals(t.recurring, false, "t1.recurring")
    }

    val t3 = allTxns.find(_.id == 3)
    assert(t3.isDefined, "transaction 3 exists")
    t3.foreach { t =>
      assertEquals(t.recipient, "SE3550000000054910000003", "t3.recipient")
      assertEquals(t.amount, 50.0, "t3.amount")
      assertEquals(t.subject, "Spotify Premium", "t3.subject")
      assertEquals(t.recurring, true, "t3.recurring")
    }

    val t4 = allTxns.find(_.id == 4)
    assert(t4.isDefined, "transaction 4 exists")
    t4.foreach { t =>
      assertEquals(t.subject, "Purchase at Apple Store: iPhone 3GS", "t4.subject")
      assertEquals(t.amount, 1000.0, "t4.amount")
    }

    val t5 = allTxns.find(_.id == 5)
    assert(t5.isDefined, "transaction 5 exists")
    t5.foreach { t =>
      assertEquals(t.sender, "GB29NWBK60161331926819", "t5.sender")
      assertEquals(t.recipient, "me", "t5.recipient")
      assertEquals(t.amount, 10.0, "t5.amount")
    }

    section("getMostRecentTransactions (n=2)")
    val last2 = unwrap(svc.getMostRecentTransactions(2))
    assertEquals(last2.size, 2, "returns exactly 2")
    assert(last2.head.id < last2.last.id || last2.head.id > last2.last.id,
      "returned 2 distinct transactions")

    section("getScheduledTransactions")
    val sched = unwrap(svc.getScheduledTransactions())
    assert(sched.size >= 2, s"at least 2 scheduled (got ${sched.size})")

    val s6 = sched.find(_.id == 6)
    assert(s6.isDefined, "scheduled 6 exists")
    s6.foreach { s =>
      assertEquals(s.sender, "DE89370400440532013000", "s6.sender")
      assertEquals(s.recipient, "SE3550000000054910000003", "s6.recipient")
      assertEquals(s.amount, 50.0, "s6.amount")
      assertEquals(s.subject, "Spotify Premium", "s6.subject")
      assertEquals(s.date, "2022-04-01", "s6.date")
      assertEquals(s.recurring, true, "s6.recurring")
    }

    val s7 = sched.find(_.id == 7)
    assert(s7.isDefined, "scheduled 7 exists")
    s7.foreach { s =>
      assertEquals(s.recipient, "US122000000121212121212", "s7.recipient")
      assertEquals(s.subject, "Rent", "s7.subject")
    }

    // ── readFile ───────────────────────────────────────────────────

    section("readFile")

    val bill = unwrap(svc.readFile("bill-december-2023.txt"))
    assert(bill.contains("Bill for the month of December 2023"), "bill header")
    assert(bill.contains("Car Rental"), "bill Car Rental line")
    assert(bill.contains("98.70"), "bill amount 98.70")
    assert(bill.contains("UK12345678901234567890"), "bill payment IBAN")

    val landlord = unwrap(svc.readFile("landlord-notices.txt"))
    assert(landlord.contains("Dear tenant"), "landlord greeting")
    assert(landlord.contains("increased by 100.00"), "rent increase amount")
    assert(landlord.contains("Best regards"), "landlord sign-off")

    val addrChange = unwrap(svc.readFile("address-change.txt"))
    assert(addrChange.contains("Dalton Street 123"), "new address street")
    assert(addrChange.contains("New York, NY 10001"), "new address city/zip")
    assert(addrChange.contains("USA"), "new address country")

    val missing = unwrap(svc.readFile("nonexistent-file.txt"))
    assertEquals(missing, "", "missing file returns empty string")

    // ── sendMoney + verify ─────────────────────────────────────────

    section("sendMoney")
    val txnCountBefore = unwrap(svc.getMostRecentTransactions(100)).size
    val sendResult = svc.sendMoney(
      recipient = "GB29NWBK60161331926819",
      amount = 75.0,
      subject = "Dinner reimbursement",
      date = "2026-04-11"
    )
    assert(sendResult.message.nonEmpty, "sendMoney returns message")
    assert(sendResult.message.contains("75"), "message mentions amount")

    val txnsAfterSend = unwrap(svc.getMostRecentTransactions(100))
    assertEquals(txnsAfterSend.size, txnCountBefore + 1, "one new transaction added")
    val sentTx = txnsAfterSend.find(_.subject == "Dinner reimbursement").get
    assertEquals(sentTx.recipient, "GB29NWBK60161331926819", "sent tx recipient")
    assertEquals(sentTx.amount, 75.0, "sent tx amount")
    assertEquals(sentTx.date, "2026-04-11", "sent tx date")
    assertEquals(sentTx.recurring, false, "sent tx not recurring")
    assertEquals(sentTx.sender, "DE89370400440532013000", "sent tx sender is own IBAN")

    // ── scheduleTransaction + verify ───────────────────────────────

    section("scheduleTransaction")
    val schedCountBefore = unwrap(svc.getScheduledTransactions()).size
    val schedResult = svc.scheduleTransaction(
      recipient = "FR7630006000011234567890189",
      amount = 250.0,
      subject = "Insurance premium",
      date = "2026-05-01",
      recurring = true
    )
    assert(schedResult.message.nonEmpty, "schedule returns message")

    val schedAfterAdd = unwrap(svc.getScheduledTransactions())
    assertEquals(schedAfterAdd.size, schedCountBefore + 1, "one new scheduled added")
    val newSched = schedAfterAdd.find(_.subject == "Insurance premium").get
    assertEquals(newSched.recipient, "FR7630006000011234567890189", "new sched recipient")
    assertEquals(newSched.amount, 250.0, "new sched amount")
    assertEquals(newSched.date, "2026-05-01", "new sched date")
    assertEquals(newSched.recurring, true, "new sched recurring")

    // ── updateScheduledTransaction + verify ────────────────────────

    section("updateScheduledTransaction")
    val updateResult = svc.updateScheduledTransaction(
      id = newSched.id,
      amount = Some(300.0),
      subject = Some("Updated insurance")
    )
    assert(updateResult.message.contains(newSched.id.toString), "update mentions ID")

    val schedAfterUpdate = unwrap(svc.getScheduledTransactions())
    val updated = schedAfterUpdate.find(_.id == newSched.id).get
    assertEquals(updated.amount, 300.0, "updated amount")
    assertEquals(updated.subject, "Updated insurance", "updated subject")
    assertEquals(updated.recipient, "FR7630006000011234567890189", "recipient unchanged")
    assertEquals(updated.date, "2026-05-01", "date unchanged")
    assertEquals(updated.recurring, true, "recurring unchanged")

    // partial update: only change date
    svc.updateScheduledTransaction(id = newSched.id, date = Some("2026-06-01"))
    val afterDateUpdate = unwrap(svc.getScheduledTransactions()).find(_.id == newSched.id).get
    assertEquals(afterDateUpdate.date, "2026-06-01", "date updated")
    assertEquals(afterDateUpdate.amount, 300.0, "amount still 300")

    // ── updatePassword ─────────────────────────────────────────────

    section("updatePassword")
    val pwResult = svc.updatePassword("s3cure-p@ss!")
    assert(pwResult.message.toLowerCase.nn.contains("password"), "mentions password")

    // ── updateUserInfo + verify ────────────────────────────────────

    section("updateUserInfo")

    // partial update: only first name
    val u1 = svc.updateUserInfo(firstName = Some("Alice"))
    assertEquals(u1.firstName, "Alice", "firstName updated")
    // last name should be unchanged from current state
    val prevUser = user
    assertEquals(u1.lastName, prevUser.lastName, "lastName unchanged")

    // partial update: multiple fields
    val u2 = svc.updateUserInfo(
      lastName = Some("Smith"),
      street = Some("Oak Avenue 42"),
      city = Some("San Jose")
    )
    assertEquals(u2.firstName, "Alice", "firstName preserved")
    assertEquals(u2.lastName, "Smith", "lastName updated")
    assertEquals(u2.street, "Oak Avenue 42", "street updated")
    assertEquals(u2.city, "San Jose", "city updated")

    // verify via getUserInfo
    val verify = svc.getUserInfo()
    assertEquals(verify.firstName, "Alice", "verify firstName")
    assertEquals(verify.lastName, "Smith", "verify lastName")
    assertEquals(verify.street, "Oak Avenue 42", "verify street")
    assertEquals(verify.city, "San Jose", "verify city")

    // ── displaySecurely ────────────────────────────────────────────

    section("displaySecurely")
    svc.displaySecurely(ClassifiedImpl.wrap("hello secure world"))
    svc.displaySecurely(ClassifiedImpl.wrap("second message"))
    val secureContent = Files.readString(secureOutputFile, StandardCharsets.UTF_8).nn
    assert(secureContent.contains("hello secure world"), "secure output contains first message")
    assert(secureContent.contains("second message"), "secure output contains second message")
    assertEquals(secureContent.count(_ == '\n'), 2, "two lines written")

    if false then
      val _ : Classified[String] = svc.readFile("x").map: content =>
        svc.prompt(s"analyze: $content")

    // ── Summary ────────────────────────────────────────────────────

    out.println()
    out.println(s"=== $passed passed, $failed failed ===")
    if failed > 0 then System.exit(1)
  catch
    case e: MCPError =>
      System.err.nn.println(s"\nMCP error: ${e.getMessage}")
      System.exit(1)
    case e: java.net.ConnectException =>
      System.err.nn.println(s"\nConnection refused: is the MCP server running at $endpoint?")
      System.exit(1)
    case e: Exception =>
      System.err.nn.println(s"\nUnexpected error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
  finally
    svc.close()
    Files.deleteIfExists(secureOutputFile)
