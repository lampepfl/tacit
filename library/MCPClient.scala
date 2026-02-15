package library.mcpclient

import java.net.{URI, HttpURLConnection}
import java.io.{BufferedReader, InputStreamReader}
import language.experimental.captureChecking

// ─── MCP Client Types ───────────────────────────────────────────────────────

/** Description of a tool available on the MCP server. */
case class MCPTool(name: String, description: String, inputSchema: Json)

/** Result of calling a tool on the MCP server. */
case class MCPToolResult(content: List[MCPTextContent], isError: Boolean)

/** A text content block returned by a tool call. */
case class MCPTextContent(text: String)

/** Error returned by the MCP server. */
case class MCPError(code: Int, message: String) extends Exception(message)

// ─── MCP Client ─────────────────────────────────────────────────────────────

/** A dependency-free MCP client for servers using the Streamable HTTP
 *  transport (the standard HTTP+SSE transport in MCP 2025-03-26).
 *
 *  The client sends JSON-RPC 2.0 POST requests to `endpoint` and handles
 *  responses in either `application/json` or `text/event-stream` format.
 *
 *  == Usage ==
 *  {{{
 *  val client = MCPClient("http://localhost:8080/mcp")
 *  client.initialize()
 *  val tools = client.listTools()
 *  val result = client.callTool("my_tool", Json.obj("arg" -> Json.str("value")))
 *  client.close()
 *  }}}
 */
class MCPClient(val endpoint: String):
  private var sessionId: String | Null = null
  private var nextId: Int = 1

  private def freshId(): Int =
    val id = nextId
    nextId += 1
    id

  /** Build a JSON-RPC 2.0 request. */
  private def rpcRequest(method: String, params: Json = Json.JNull): (Int, String) =
    val id = freshId()
    val fields = scala.collection.mutable.ListBuffer(
      "jsonrpc" -> Json.str("2.0"),
      "method" -> Json.str(method),
      "id" -> Json.JNum(id)
    )
    if !params.isNull then
      fields += ("params" -> params)
    (id, Json.JObj(fields.toList).render)

  /** Send a JSON-RPC request and return the parsed result. */
  private def send(method: String, params: Json = Json.JNull): Json =
    val (id, body) = rpcRequest(method, params)
    val uri = URI(endpoint)
    val conn = uri.toURL.openConnection().asInstanceOf[HttpURLConnection]
    try
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setRequestProperty("Content-Type", "application/json")
      conn.setRequestProperty("Accept", "application/json, text/event-stream")
      if sessionId != null then
        conn.setRequestProperty("Mcp-Session-Id", sessionId)
      conn.setConnectTimeout(30000)
      conn.setReadTimeout(60000)

      val os = conn.getOutputStream
      try os.write(body.getBytes("UTF-8"))
      finally os.close()

      val code = conn.getResponseCode
      if code < 200 || code >= 300 then
        val errStream = conn.getErrorStream
        val errBody = if errStream != null then
          try String(errStream.readAllBytes(), "UTF-8")
          finally errStream.close()
        else ""
        throw MCPError(-1, s"HTTP $code: $errBody")

      // Capture session ID from response
      val sid = conn.getHeaderField("Mcp-Session-Id")
      if sid != null then sessionId = sid

      val contentType = Option(conn.getContentType).getOrElse("")
      val is = conn.getInputStream
      try
        if contentType.contains("text/event-stream") then
          parseSSE(is, id)
        else
          val raw = String(is.readAllBytes(), "UTF-8")
          extractResult(Json.parse(raw), id)
      finally is.close()
    finally conn.disconnect()

  /** Parse an SSE stream looking for the JSON-RPC response matching `id`. */
  private def parseSSE(is: java.io.InputStream, id: Int): Json =
    val reader = BufferedReader(InputStreamReader(is, "UTF-8"))
    var data = StringBuilder()
    var found: Json | Null = null
    var line = reader.readLine()
    while line != null && found == null do
      if line.startsWith("data: ") then
        data.append(line.substring(6))
      else if line.isEmpty && data.nonEmpty then
        // End of event
        val json = Json.parse(data.toString)
        data.clear()
        // Could be a single response or an array (batch)
        json match
          case Json.JArr(items) =>
            items.foreach: item =>
              val rid = item("id").asInt
              if rid.contains(id) then found = extractResult(item, id)
          case obj =>
            val rid = obj("id").asInt
            if rid.contains(id) then found = extractResult(obj, id)
      else if line.isEmpty then
        data.clear()

      line = reader.readLine()
    end while

    // Process any remaining data
    if found == null && data.nonEmpty then
      val json = Json.parse(data.toString)
      found = extractResult(json, id)

    if found == null then
      throw MCPError(-1, "No response received for request id " + id)
    found.nn

  /** Extract the `result` field from a JSON-RPC response, or throw on error. */
  private def extractResult(response: Json, expectedId: Int): Json =
    val error = response("error")
    if !error.isNull then
      val code = error("code").asInt.getOrElse(-1)
      val msg = error("message").asString.getOrElse("Unknown error")
      throw MCPError(code, msg)
    response("result")

  // ── Public API ──────────────────────────────────────────────────────────

  /** Initialize the MCP session. Must be called before other methods.
   *  Sends `initialize` followed by `notifications/initialized`. */
  def initialize(): Json =
    val result = send("initialize", Json.obj(
      "protocolVersion" -> Json.str("2025-03-26"),
      "capabilities" -> Json.obj(),
      "clientInfo" -> Json.obj(
        "name" -> Json.str("library-mcp-client"),
        "version" -> Json.str("0.1.0")
      )
    ))
    // Send initialized notification (no id, no response expected)
    sendNotification("notifications/initialized")
    result

  /** Send a JSON-RPC notification (no id, no response). */
  private def sendNotification(method: String, params: Json = Json.JNull): Unit =
    val fields = scala.collection.mutable.ListBuffer(
      "jsonrpc" -> Json.str("2.0"),
      "method" -> Json.str(method)
    )
    if !params.isNull then
      fields += ("params" -> params)
    val body = Json.JObj(fields.toList).render

    val uri = URI(endpoint)
    val conn = uri.toURL.openConnection().asInstanceOf[HttpURLConnection]
    try
      conn.setRequestMethod("POST")
      conn.setDoOutput(true)
      conn.setRequestProperty("Content-Type", "application/json")
      conn.setRequestProperty("Accept", "application/json, text/event-stream")
      if sessionId != null then
        conn.setRequestProperty("Mcp-Session-Id", sessionId)
      conn.setConnectTimeout(30000)
      conn.setReadTimeout(10000)

      val os = conn.getOutputStream
      try os.write(body.getBytes("UTF-8"))
      finally os.close()

      // Read and discard any response
      val code = conn.getResponseCode
      if code >= 200 && code < 300 then
        val is = conn.getInputStream
        try is.readAllBytes()
        finally is.close()
    finally conn.disconnect()

  /** List all tools available on the server. */
  def listTools(): List[MCPTool] =
    val result = send("tools/list")
    result("tools").asArray.getOrElse(Nil).map: t =>
      MCPTool(
        name = t("name").asString.getOrElse(""),
        description = t("description").asString.getOrElse(""),
        inputSchema = t("inputSchema")
      )

  /** Call a tool by name with the given arguments.
   *
   *  @param name      the tool name
   *  @param arguments a JSON object of tool arguments */
  def callTool(name: String, arguments: Json = Json.obj()): MCPToolResult =
    val result = send("tools/call", Json.obj(
      "name" -> Json.str(name),
      "arguments" -> arguments
    ))
    val content = result("content").asArray.getOrElse(Nil).collect:
      case c if c("type").asString.contains("text") =>
        MCPTextContent(c("text").asString.getOrElse(""))
    val isError = result("isError").asBool.getOrElse(false)
    MCPToolResult(content, isError)

  /** Terminate the MCP session by sending DELETE to the endpoint. */
  def close(): Unit =
    if sessionId != null then
      val uri = URI(endpoint)
      val conn = uri.toURL.openConnection().asInstanceOf[HttpURLConnection]
      try
        conn.setRequestMethod("DELETE")
        conn.setRequestProperty("Mcp-Session-Id", sessionId)
        conn.setConnectTimeout(5000)
        conn.setReadTimeout(5000)
        conn.getResponseCode // fire and forget
      catch case _: Exception => () // ignore errors on close
      finally conn.disconnect()
    sessionId = null
