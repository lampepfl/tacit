import tacit.mcp.*
import io.circe.*
import io.circe.syntax.*
import tacit.core.{Context, Config}

class McpServerSuite extends munit.FunSuite:
  given defaultTestCtx: Context = Context(Config(), None)

  // ── Helpers ──────────────────────────────────────────────────────────

  private def makeRequest(method: String, params: Option[Json] = None, id: Option[Json] = Some(Json.fromInt(1))): JsonRpcRequest =
    JsonRpcRequest("2.0", method, params, id)

  private def toolCallRequest(name: String, arguments: Json = Json.obj()): JsonRpcRequest =
    JsonRpcRequest("2.0", "tools/call", Some(Json.obj("name" -> name.asJson, "arguments" -> arguments.asJson)), Some(Json.fromInt(1)))

  private def extractContent(response: Option[JsonRpcResponse]): Option[String] =
    response.flatMap(_.result).flatMap(_.hcursor.get[List[Json]]("content").toOption)
      .flatMap(_.headOption).flatMap(_.hcursor.get[String]("text").toOption)

  private def hasIsError(response: Option[JsonRpcResponse]): Boolean =
    response.flatMap(_.result).flatMap(_.hcursor.get[Boolean]("isError").toOption).getOrElse(false)

  private def extractSessionId(response: Option[JsonRpcResponse]): String =
    extractContent(response).get.split(": ").last.trim

  // ── Initialize and protocol basics ──────────────────────────────────

  test("initialize request"):
    val server = new McpServer()
    val request = JsonRpcRequest(
      jsonrpc = "2.0",
      method = "initialize",
      params = Some(Json.obj(
        "protocolVersion" -> "2024-11-05".asJson,
        "capabilities" -> Json.obj(),
        "clientInfo" -> Json.obj(
          "name" -> "test-client".asJson,
          "version" -> "1.0.0".asJson
        )
      )),
      id = Some(Json.fromInt(1))
    )

    val response = server.handleRequest(request)
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    assert(response.get.result.isDefined)

  test("tools/list request"):
    val server = new McpServer()
    val request = JsonRpcRequest(
      jsonrpc = "2.0",
      method = "tools/list",
      params = None,
      id = Some(Json.fromInt(1))
    )

    val response = server.handleRequest(request)
    assert(response.isDefined)
    assert(response.get.error.isEmpty)

    val tools = response.get.result.flatMap(_.hcursor.get[List[Json]]("tools").toOption)
    assert(tools.isDefined)
    assert(tools.get.nonEmpty)

  test("execute_scala tool"):
    val server = new McpServer()
    val request = JsonRpcRequest(
      jsonrpc = "2.0",
      method = "tools/call",
      params = Some(Json.obj(
        "name" -> "execute_scala".asJson,
        "arguments" -> Json.obj(
          "code" -> "1 + 1".asJson
        )
      )),
      id = Some(Json.fromInt(1))
    )

    val response = server.handleRequest(request)
    assert(response.isDefined)
    assert(response.get.error.isEmpty)

  test("create and use repl session"):
    val server = new McpServer()

    // Create session
    val createRequest = JsonRpcRequest(
      jsonrpc = "2.0",
      method = "tools/call",
      params = Some(Json.obj(
        "name" -> "create_repl_session".asJson,
        "arguments" -> Json.obj()
      )),
      id = Some(Json.fromInt(1))
    )

    val createResponse = server.handleRequest(createRequest)
    assert(createResponse.isDefined)
    assert(createResponse.get.error.isEmpty)

    // Extract session ID from response
    val content = createResponse.get.result
      .flatMap(_.hcursor.get[List[Json]]("content").toOption)
      .flatMap(_.headOption)
      .flatMap(_.hcursor.get[String]("text").toOption)

    assert(content.isDefined)
    val sessionId = content.get.split(": ").last.trim

    // Execute in session
    val execRequest = JsonRpcRequest(
      jsonrpc = "2.0",
      method = "tools/call",
      params = Some(Json.obj(
        "name" -> "execute_in_session".asJson,
        "arguments" -> Json.obj(
          "session_id" -> sessionId.asJson,
          "code" -> "val y = 100".asJson
        )
      )),
      id = Some(Json.fromInt(2))
    )

    val execResponse = server.handleRequest(execRequest)
    assert(execResponse.isDefined)
    assert(execResponse.get.error.isEmpty)

  // ── Protocol handling ─────────────────────────────────────────────

  test("ping returns empty result"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("ping"))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    assert(response.get.result.isDefined)
    assertEquals(response.get.result.get, Json.obj())

  test("unknown method returns MethodNotFound error"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("foo/bar"))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.MethodNotFound)

  test("notifications/initialized returns None"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("notifications/initialized", id = None))
    assert(response.isEmpty)

  test("initialized (legacy) returns None"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("initialized", id = None))
    assert(response.isEmpty)

  test("notifications/cancelled returns None"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("notifications/cancelled", id = None))
    assert(response.isEmpty)

  test("arbitrary notification returns None"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("notifications/whatever", id = None))
    assert(response.isEmpty)

  // ── Error handling ───────────────────────────────────────────────

  test("tools/call with missing params returns error"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("tools/call", params = None))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  test("tools/call with malformed params returns error"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("tools/call", params = Some(Json.obj("bad" -> true.asJson))))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  test("tools/call with unknown tool name returns error"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("nonexistent_tool"))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  test("execute_scala with missing code argument returns error"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("execute_scala", Json.obj()))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  // ── Security ────────────────────────────────────────────────────

  test("execute_scala with forbidden code returns isError"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("execute_scala", Json.obj("code" -> "import java.io.File".asJson)))
    assert(response.isDefined)
    assert(response.get.error.isEmpty, s"Expected no protocol error but got: ${response.get.error}")
    assert(hasIsError(response), "Expected isError=true in tool result")
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.toLowerCase.contains("forbidden") || text.get.toLowerCase.contains("error") || text.get.toLowerCase.contains("violation") || text.get.toLowerCase.contains("blocked"),
      s"Expected violation message but got: ${text.get}")

  test("execute_in_session with forbidden code returns isError"):
    val server = new McpServer()
    val createResponse = server.handleRequest(toolCallRequest("create_repl_session"))
    val sessionId = extractSessionId(createResponse)
    val response = server.handleRequest(toolCallRequest("execute_in_session", Json.obj(
      "session_id" -> sessionId.asJson,
      "code" -> "import java.io.File".asJson
    )))
    assert(response.isDefined)
    assert(response.get.error.isEmpty, s"Expected no protocol error but got: ${response.get.error}")
    assert(hasIsError(response), "Expected isError=true in tool result")
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.toLowerCase.contains("forbidden") || text.get.toLowerCase.contains("error") || text.get.toLowerCase.contains("violation") || text.get.toLowerCase.contains("blocked"),
      s"Expected violation message but got: ${text.get}")

  // ── Tool coverage ───────────────────────────────────────────────

  test("delete_repl_session with valid session"):
    val server = new McpServer()
    val createResponse = server.handleRequest(toolCallRequest("create_repl_session"))
    val sessionId = extractSessionId(createResponse)
    val deleteResponse = server.handleRequest(toolCallRequest("delete_repl_session", Json.obj("session_id" -> sessionId.asJson)))
    assert(deleteResponse.isDefined)
    assert(deleteResponse.get.error.isEmpty)
    assert(!hasIsError(deleteResponse))
    val text = extractContent(deleteResponse)
    assert(text.isDefined)
    assert(text.get.contains("Deleted session"))

  test("delete_repl_session with non-existent session returns isError"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("delete_repl_session", Json.obj("session_id" -> "no-such-session".asJson)))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    assert(hasIsError(response))

  test("delete_repl_session with missing session_id returns error"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("delete_repl_session", Json.obj()))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  test("list_sessions with no sessions"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("list_sessions"))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.contains("No active sessions"))

  test("list_sessions with multiple sessions"):
    val server = new McpServer()
    val resp1 = server.handleRequest(toolCallRequest("create_repl_session"))
    val id1 = extractSessionId(resp1)
    val resp2 = server.handleRequest(toolCallRequest("create_repl_session"))
    val id2 = extractSessionId(resp2)

    val listResponse = server.handleRequest(toolCallRequest("list_sessions"))
    assert(listResponse.isDefined)
    assert(listResponse.get.error.isEmpty)
    val text = extractContent(listResponse)
    assert(text.isDefined)
    assert(text.get.contains(id1), s"Expected list to contain $id1 but got: ${text.get}")
    assert(text.get.contains(id2), s"Expected list to contain $id2 but got: ${text.get}")

  test("show_interface returns interface documentation"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("show_interface"))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.contains("requestFileSystem"), s"Expected 'requestFileSystem' in interface docs but got: ${text.get.take(200)}")

  // ── Session disabled mode ───────────────────────────────────────

  test("tools/list omits session tools when sessionEnabled=false"):
    given Context = Context(Config(sessionEnabled = false), None)
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("tools/list"))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)

    val toolNames = response.get.result
      .flatMap(_.hcursor.get[List[Json]]("tools").toOption)
      .getOrElse(Nil)
      .flatMap(_.hcursor.get[String]("name").toOption)

    assert(toolNames.contains("execute_scala"), s"Expected execute_scala in $toolNames")
    assert(toolNames.contains("show_interface"), s"Expected show_interface in $toolNames")
    assert(!toolNames.contains("create_repl_session"), s"Expected no create_repl_session in $toolNames")
    assert(!toolNames.contains("execute_in_session"), s"Expected no execute_in_session in $toolNames")
    assert(!toolNames.contains("delete_repl_session"), s"Expected no delete_repl_session in $toolNames")
    assert(!toolNames.contains("list_sessions"), s"Expected no list_sessions in $toolNames")

  test("create_repl_session returns error when sessions disabled"):
    given Context = Context(Config(sessionEnabled = false), None)
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("create_repl_session"))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assert(response.get.error.get.message.toLowerCase.contains("disabled") || response.get.error.get.message.toLowerCase.contains("session"),
      s"Expected error about sessions being disabled but got: ${response.get.error.get.message}")
