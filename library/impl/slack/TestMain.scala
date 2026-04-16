package tacit.library.slack

import language.experimental.captureChecking

import tacit.library.{Classified, ClassifiedImpl, IOCapability}
import tacit.library.mcp.MCPError
import caps.unsafe.unsafeAssumePure

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

@main def TestSlack(args: String*): Unit =
  val argList = args.toList
  val port = argList.sliding(2).collectFirst {
    case "--agentdojo-port" :: p :: Nil => p.toInt
  }.getOrElse(49593)
  val endpoint = s"http://localhost:$port/mcp"
  val out = System.out.nn
  out.println(s"=== Slack Service Test (endpoint: $endpoint) ===")

  val secureOutputFile = Files.createTempFile("slack-secure-", ".txt").nn
  val svc = SlackImpl(endpoint, secureOutputFile.toString)
  val testTag = System.currentTimeMillis.toString
  val uniqueUser = s"TacitSlack$testTag"
  val uniqueEmail = s"tacit.slack.$testTag@example.com"
  val uniqueDm = s"direct-message-$testTag"
  val emptyDm = ""
  val uniqueChannelBody = s"channel-message-$testTag"
  val externalChannelBody = s"external-channel-message-$testTag"
  val uniqueUrl = s"www.tacit-slack-$testTag.com"
  val uniqueWebContent = s"slack webpage content $testTag\nwith second line and unicode: 日本語"
  val overwrittenWebContent = s"overwritten slack webpage content $testTag"
  val emptyUrl = s"www.tacit-slack-empty-$testTag.com"
  given (IOCapability^{}) = IOCapability.iocap.unsafeAssumePure

  try
    // ── Channel / membership reads ───────────────────────────────

    section("getChannels")
    val channels = unwrap(svc.getChannels())
    assertEquals(channels.size, 4, "channel count")
    assert(channels.contains("general"), "general channel exists")
    assert(channels.contains("random"), "random channel exists")
    assert(channels.contains("private"), "private channel exists")
    assertEquals(unwrap(svc.getChannels()), channels, "getChannels idempotent")
    assertEquals(svc.getChannels().toString, "Classified(***)", "channels are classified")
    val externalChannel = channels.find(_.startsWith("External_"))
    assert(externalChannel.isDefined, "external channel exists")
    val ext = externalChannel.getOrElse("")

    section("getUsersInChannel")
    assertEquals(unwrap(svc.getUsersInChannel("general")).sorted, List("Alice", "Bob", "Charlie", "Eve"), "general members")
    assertEquals(unwrap(svc.getUsersInChannel("random")).sorted, List("Alice", "Bob"), "random members")
    assertEquals(unwrap(svc.getUsersInChannel("private")), List("Charlie"), "private members")
    assertEquals(unwrap(svc.getUsersInChannel(ext)), List("Eve"), "external members")
    assertEquals(svc.getUsersInChannel("general").toString, "Classified(***)", "channel membership is classified")

    // ── Message reads ────────────────────────────────────────────

    section("readChannelMessages")
    val general = unwrap(svc.readChannelMessages("general"))
    assertEquals(general.size, 3, "general message count")
    assert(general.exists(_.body.contains("Secret key is 1a7b3d")), "general secret key message")
    assert(general.exists(_.body.contains("www.informations.com")), "general article link")
    assert(general.exists(_.body.contains("www.restaurant-zurich.com")), "general restaurant link")
    val secretMsg = general.find(_.body.contains("Secret key is 1a7b3d"))
    assert(secretMsg.isDefined, "secret message exists")
    secretMsg.foreach { msg =>
      assertEquals(msg.sender, "Charlie", "secret message sender")
      assertEquals(msg.recipient, "general", "secret message recipient")
    }

    val random = unwrap(svc.readChannelMessages("random"))
    assertEquals(random.size, 1, "random message count")
    assertEquals(random.head.sender, "Alice", "random sender")
    assertEquals(random.head.recipient, "random", "random recipient")
    assertEquals(random.head.body, "Has anyone seen my coffee mug?", "random body")

    val privateChannel = unwrap(svc.readChannelMessages("private"))
    assertEquals(privateChannel.size, 1, "private message count")
    assertEquals(privateChannel.head.sender, "Charlie", "private sender")
    assertEquals(privateChannel.head.recipient, "private", "private recipient")
    assertEquals(privateChannel.head.body, "just testing", "private body")

    val external = unwrap(svc.readChannelMessages(ext))
    assertEquals(external, Nil, "external channel starts empty")
    assertEquals(svc.readChannelMessages("general").toString, "Classified(***)", "channel messages are classified")

    section("readInbox")
    val aliceInbox = unwrap(svc.readInbox("Alice"))
    assertEquals(aliceInbox.size, 1, "Alice inbox count")
    assertEquals(aliceInbox.head.sender, "Bob", "Alice inbox sender")
    assertEquals(aliceInbox.head.recipient, "Alice", "Alice inbox recipient")
    assert(aliceInbox.head.body.contains("invite Dora to Slack"), "Alice inbox body")

    val bobInbox = unwrap(svc.readInbox("Bob"))
    assertEquals(bobInbox.size, 3, "Bob inbox count")
    assert(bobInbox.exists(_.body.contains("painting")), "Bob inbox painting")
    assert(bobInbox.exists(_.body.contains("swimming")), "Bob inbox swimming")
    assert(bobInbox.exists(_.body.contains("www.eve-blog.com")), "Bob inbox Eve link")
    val eveHobby = bobInbox.find(_.body.contains("www.eve-blog.com"))
    assert(eveHobby.isDefined, "Bob inbox Eve message exists")
    eveHobby.foreach { msg =>
      assertEquals(msg.sender, "Eve", "Bob inbox Eve sender")
      assertEquals(msg.recipient, "Bob", "Bob inbox Eve recipient")
    }

    assertEquals(unwrap(svc.readInbox("Charlie")), Nil, "Charlie inbox empty")
    assertEquals(unwrap(svc.readInbox("Eve")), Nil, "Eve inbox empty")
    assertEquals(svc.readInbox("Alice").toString, "Classified(***)", "inbox is classified")

    // ── Web reads ────────────────────────────────────────────────

    section("getWebpage")
    val article = svc.getWebpage("www.informations.com")
    assertEquals(article.toString, "Classified(***)", "classified toString redacted")
    val articleText = unwrap(article)
    assert(articleText.contains("7.2%"), "article content")
    assertEquals(unwrap(svc.getWebpage("https://www.informations.com")), articleText, "https URL normalized")
    assertEquals(unwrap(svc.getWebpage("http://www.informations.com")), articleText, "http URL normalized")

    val missingPage = unwrap(svc.getWebpage("www.tacit-missing-page.com"))
    assertEquals(missingPage, "404 Not Found", "missing webpage sentinel")

    // ── Mutations ────────────────────────────────────────────────

    section("inviteUserToSlack")
    svc.inviteUserToSlack(uniqueUser, uniqueEmail)
    val invitedInbox = unwrap(svc.readInbox(uniqueUser))
    assertEquals(invitedInbox, Nil, "newly invited user inbox empty")
    assert(!unwrap(svc.getUsersInChannel("general")).contains(uniqueUser), "newly invited user not yet in general")
    assertEquals(unwrap(svc.getChannels()), channels, "inviting user does not change channel list")

    section("addUserToChannel")
    svc.addUserToChannel(uniqueUser, "general")
    assert(unwrap(svc.getUsersInChannel("general")).contains(uniqueUser), "invited user added to general")
    svc.addUserToChannel(uniqueUser, ext)
    assert(unwrap(svc.getUsersInChannel(ext)).contains(uniqueUser), "invited user added to external channel")

    section("sendDirectMessage")
    svc.sendDirectMessage(uniqueUser, uniqueDm)
    svc.sendDirectMessage(uniqueUser, emptyDm)
    val dmInbox = unwrap(svc.readInbox(uniqueUser))
    val dm = dmInbox.find(_.body == uniqueDm)
    assert(dm.isDefined, "direct message appended")
    dm.foreach { msg =>
      assertEquals(msg.sender, "bot", "direct message sender")
      assertEquals(msg.recipient, uniqueUser, "direct message recipient")
    }
    assert(dmInbox.exists(_.body == ""), "empty direct message accepted")
    assertEquals(dmInbox.last.body, "", "empty direct message appended last")

    section("sendChannelMessage")
    svc.sendChannelMessage("general", uniqueChannelBody)
    svc.sendChannelMessage(ext, externalChannelBody)
    val generalAfterPost = unwrap(svc.readChannelMessages("general"))
    val posted = generalAfterPost.find(_.body == uniqueChannelBody)
    assert(posted.isDefined, "channel message appended")
    posted.foreach { msg =>
      assertEquals(msg.sender, "bot", "channel message sender")
      assertEquals(msg.recipient, "general", "channel message recipient")
    }
    val externalAfterPost = unwrap(svc.readChannelMessages(ext))
    val externalPosted = externalAfterPost.find(_.body == externalChannelBody)
    assert(externalPosted.isDefined, "external channel message appended")
    externalPosted.foreach { msg =>
      assertEquals(msg.sender, "bot", "external channel sender")
      assertEquals(msg.recipient, ext, "external channel recipient")
    }

    section("postWebpage")
    svc.postWebpage(s"https://$uniqueUrl", uniqueWebContent)
    assertEquals(unwrap(svc.getWebpage(uniqueUrl)), uniqueWebContent, "posted webpage round-trip")
    assertEquals(unwrap(svc.getWebpage(s"http://$uniqueUrl")), uniqueWebContent, "posted webpage URL normalization")
    svc.postWebpage(uniqueUrl, overwrittenWebContent)
    assertEquals(unwrap(svc.getWebpage(uniqueUrl)), overwrittenWebContent, "postWebpage overwrite")
    svc.postWebpage(emptyUrl, "")
    assertEquals(unwrap(svc.getWebpage(emptyUrl)), "", "empty webpage content preserved")

    section("removeUserFromSlack")
    svc.removeUserFromSlack(uniqueUser)
    assert(!unwrap(svc.getUsersInChannel("general")).contains(uniqueUser), "removed user no longer in general")
    assert(!unwrap(svc.getUsersInChannel(ext)).contains(uniqueUser), "removed user no longer in external channel")
    expectError("removed user inbox")(unwrap(svc.readInbox(uniqueUser)))
    expectError("removed user DM")(svc.sendDirectMessage(uniqueUser, "after removal"))
    expectError("removed user add to channel")(svc.addUserToChannel(uniqueUser, "general"))

    section("reinvite after removal")
    svc.inviteUserToSlack(uniqueUser, uniqueEmail)
    assertEquals(unwrap(svc.readInbox(uniqueUser)), Nil, "reinvited user inbox reset")
    svc.addUserToChannel(uniqueUser, "random")
    assert(unwrap(svc.getUsersInChannel("random")).contains(uniqueUser), "reinvited user added to random")
    svc.removeUserFromSlack(uniqueUser)
    expectError("removed user inbox after reinvite")(unwrap(svc.readInbox(uniqueUser)))

    // ── Errors ───────────────────────────────────────────────────

    section("error cases")
    expectError("readChannelMessages missing channel")(unwrap(svc.readChannelMessages("missing-channel")))
    expectError("readInbox missing user")(unwrap(svc.readInbox("MissingUser")))
    expectError("sendDirectMessage missing recipient")(svc.sendDirectMessage("MissingUser", "hi"))
    expectError("sendChannelMessage missing channel")(svc.sendChannelMessage("missing-channel", "hi"))
    expectError("getUsersInChannel missing channel")(unwrap(svc.getUsersInChannel("missing-channel")))
    expectError("addUserToChannel missing user")(svc.addUserToChannel("MissingUser", "general"))
    expectError("addUserToChannel missing channel")(svc.addUserToChannel("Alice", "missing-channel"))
    expectError("inviteUserToSlack duplicate user")(svc.inviteUserToSlack("Alice", "alice@example.com"))
    expectError("removeUserFromSlack missing user")(svc.removeUserFromSlack("MissingUser"))

    // ── displaySecurely ──────────────────────────────────────────

    section("displaySecurely")
    val secureHeadline = svc.getWebpage("www.informations.com").map(_.take(32))
    svc.displaySecurely(secureHeadline)
    svc.displaySecurely(ClassifiedImpl.wrap("hello secure slack"))
    val secureContent = Files.readString(secureOutputFile, StandardCharsets.UTF_8).nn
    assert(secureContent.contains("The latest job report"), "secure output contains webpage excerpt")
    assert(secureContent.contains("hello secure slack"), "secure output contains explicit message")
    assertEquals(secureContent.count(_ == '\n'), 2, "two secure output lines")

    if false then
      val _ : Classified[String] = svc.readInbox("Alice").map: inbox =>
        svc.prompt(s"Summarize these inbox messages: $inbox")

    out.println()
    out.println(s"=== $passed passed, $failed failed ===")
    if failed > 0 then System.exit(1)
  catch
    case e: MCPError =>
      System.err.nn.println(s"\nMCP error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
    case _: java.net.ConnectException =>
      System.err.nn.println(s"\nConnection refused: is the slack MCP server running at $endpoint?")
      System.exit(1)
    case e: Exception =>
      System.err.nn.println(s"\nUnexpected error: ${e.getMessage}")
      e.printStackTrace()
      System.exit(1)
  finally
    svc.close()
    Files.deleteIfExists(secureOutputFile)
