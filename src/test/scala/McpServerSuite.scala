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

  test("tools/list returns exactly one tool"):
    val server = new McpServer()
    val response = server.handleRequest(makeRequest("tools/list"))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)

    val tools = response.get.result.flatMap(_.hcursor.get[List[Json]]("tools").toOption)
    assert(tools.isDefined)
    val names = tools.get.flatMap(_.hcursor.get[String]("name").toOption)
    assertEquals(names, List("eval_scala"))

  test("eval_scala tool executes code"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("eval_scala", Json.obj("code" -> "1 + 1".asJson)))
    assert(response.isDefined)
    assert(response.get.error.isEmpty)
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.contains("2"), s"Expected '2' in output, got: ${text.get}")

  test("eval_scala tool maintains state across calls"):
    val server = new McpServer()
    val r1 = server.handleRequest(toolCallRequest("eval_scala", Json.obj("code" -> "val x = 42".asJson)))
    assert(r1.get.error.isEmpty)
    assert(!hasIsError(r1))
    val r2 = server.handleRequest(toolCallRequest("eval_scala", Json.obj("code" -> "x * 2".asJson)))
    assert(r2.get.error.isEmpty)
    val text = extractContent(r2)
    assert(text.isDefined)
    assert(text.get.contains("84"), s"Expected '84' (proving state persists), got: ${text.get}")

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

  test("eval_scala with missing code argument returns error"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("eval_scala", Json.obj()))
    assert(response.isDefined)
    assert(response.get.error.isDefined)
    assertEquals(response.get.error.get.code, JsonRpcError.InvalidParams)

  // ── Security ────────────────────────────────────────────────────

  test("eval_scala with forbidden code returns isError"):
    val server = new McpServer()
    val response = server.handleRequest(toolCallRequest("eval_scala", Json.obj("code" -> "import java.io.File".asJson)))
    assert(response.isDefined)
    assert(response.get.error.isEmpty, s"Expected no protocol error but got: ${response.get.error}")
    assert(hasIsError(response), "Expected isError=true in tool result")
    val text = extractContent(response)
    assert(text.isDefined)
    assert(text.get.toLowerCase.contains("forbidden") || text.get.toLowerCase.contains("error") || text.get.toLowerCase.contains("violation") || text.get.toLowerCase.contains("blocked"),
      s"Expected violation message but got: ${text.get}")
