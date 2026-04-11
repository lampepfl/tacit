package tacit.library.banking.mcp

@main def TestMCPClient(args: String*): Unit =
  val endpoint = args.headOption.getOrElse("http://localhost:61954/mcp")
  val out = System.out.nn
  out.println(s"=== Banking Service Test (endpoint: $endpoint) ===")
  out.println()

  val svc = BankingService(endpoint)
  try
    // 1. Get IBAN
    out.println("1. getIban()")
    val iban = svc.getIban()
    out.println(s"   IBAN: $iban")
    out.println()

    // 2. Get balance
    out.println("2. getBalance()")
    val balance = svc.getBalance()
    out.println(s"   Balance: $balance")
    out.println()

    // 3. Get user info
    out.println("3. getUserInfo()")
    val user = svc.getUserInfo()
    out.println(s"   Name: ${user.first_name} ${user.last_name}")
    out.println(s"   Address: ${user.street}, ${user.city}")
    out.println()

    // 4. Get recent transactions
    out.println("4. getMostRecentTransactions(5)")
    val txns = svc.getMostRecentTransactions(5)
    txns.foreach { t =>
      out.println(s"   [${t.id}] ${t.sender} -> ${t.recipient}: ${t.amount} (${t.subject}, ${t.date}, recurring=${t.recurring})")
    }
    if txns.isEmpty then out.println("   (none)")
    out.println()

    // 5. Get scheduled transactions
    out.println("5. getScheduledTransactions()")
    val scheduled = svc.getScheduledTransactions()
    scheduled.foreach { t =>
      out.println(s"   [${t.id}] ${t.sender} -> ${t.recipient}: ${t.amount} (${t.subject}, ${t.date}, recurring=${t.recurring})")
    }
    if scheduled.isEmpty then out.println("   (none)")
    out.println()

    // 6. Send money
    out.println("6. sendMoney()")
    val sendResult = svc.sendMoney(
      recipient = "DE89370400440532013000",
      amount = 42.50,
      subject = "Test payment",
      date = "2026-04-11"
    )
    out.println(s"   ${sendResult.message}")
    out.println()

    // 7. Schedule transaction
    out.println("7. scheduleTransaction()")
    val schedResult = svc.scheduleTransaction(
      recipient = "DE89370400440532013000",
      amount = 100.0,
      subject = "Monthly rent",
      date = "2026-05-01",
      recurring = true
    )
    out.println(s"   ${schedResult.message}")
    out.println()

    // 8. Verify new transactions appear
    out.println("8. Verify: getScheduledTransactions() after scheduling")
    val scheduledAfter = svc.getScheduledTransactions()
    scheduledAfter.foreach { t =>
      out.println(s"   [${t.id}] ${t.sender} -> ${t.recipient}: ${t.amount} (${t.subject}, ${t.date}, recurring=${t.recurring})")
    }
    out.println()

    // 9. Update scheduled transaction (use the last one we just created)
    val lastScheduled = scheduledAfter.last
    out.println(s"9. updateScheduledTransaction(id=${lastScheduled.id}, amount=150.0)")
    val updateResult = svc.updateScheduledTransaction(
      id = lastScheduled.id,
      amount = Some(150.0)
    )
    out.println(s"   ${updateResult.message}")
    out.println()

    // 10. Update password
    out.println("10. updatePassword()")
    val pwResult = svc.updatePassword("new-secure-password-123")
    out.println(s"    ${pwResult.message}")
    out.println()

    // 11. Update user info
    out.println("11. updateUserInfo(city=San Francisco)")
    val updatedUser = svc.updateUserInfo(city = Some("San Francisco"))
    out.println(s"    Name: ${updatedUser.first_name} ${updatedUser.last_name}")
    out.println(s"    Address: ${updatedUser.street}, ${updatedUser.city}")
    out.println()

    // 12. Read file
    out.println("12. readFile()")
    try
      val content = svc.readFile("test.txt")
      out.println(s"    Content: ${content.take(200)}")
    catch
      case e: MCPError => out.println(s"    (expected) $e")
    out.println()

    out.println("=== All 11 tools tested successfully ===")
  catch
    case e: MCPError =>
      System.err.nn.println(s"MCP error: ${e.getMessage}")
    case e: java.net.ConnectException =>
      System.err.nn.println(s"Connection refused: is the MCP server running at $endpoint?")
    case e: Exception =>
      System.err.nn.println(s"Unexpected error: ${e.getMessage}")
      e.printStackTrace()
  finally
    svc.close()
