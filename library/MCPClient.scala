package library.mcpclient

import java.net.{URI, HttpURLConnection}
import java.io.{BufferedReader, InputStreamReader}
import language.experimental.captureChecking

// ─── Minimal JSON ────────────────────────────────────────────────────────────

/** A minimal, dependency-free JSON representation. */
enum Json:
  case JNull
  case JBool(value: Boolean)
  case JNum(value: Double)
  case JStr(value: String)
  case JArr(values: List[Json])
  case JObj(fields: List[(String, Json)])

  def apply(key: String): Json = this match
    case JObj(fields) =>
      fields.find(_._1 == key).map(_._2).getOrElse(JNull)
    case _ => JNull

  def asString: Option[String] = this match
    case JStr(s) => Some(s)
    case _ => None

  def asDouble: Option[Double] = this match
    case JNum(n) => Some(n)
    case _ => None

  def asInt: Option[Int] = asDouble.map(_.toInt)

  def asBool: Option[Boolean] = this match
    case JBool(b) => Some(b)
    case _ => None

  def asArray: Option[List[Json]] = this match
    case JArr(vs) => Some(vs)
    case _ => None

  def asObject: Option[List[(String, Json)]] = this match
    case JObj(fs) => Some(fs)
    case _ => None

  def isNull: Boolean = this == JNull

  def render: String = this match
    case JNull => "null"
    case JBool(b) => b.toString
    case JNum(n) =>
      if n == n.toLong then n.toLong.toString else n.toString
    case JStr(s) => "\"" + escapeJson(s) + "\""
    case JArr(vs) => vs.map(_.render).mkString("[", ",", "]")
    case JObj(fs) =>
      fs.map((k, v) => "\"" + escapeJson(k) + "\":" + v.render).mkString("{", ",", "}")

  private def escapeJson(s: String): String =
    val sb = StringBuilder()
    s.foreach:
      case '"'  => sb.append("\\\"")
      case '\\' => sb.append("\\\\")
      case '\b' => sb.append("\\b")
      case '\f' => sb.append("\\f")
      case '\n' => sb.append("\\n")
      case '\r' => sb.append("\\r")
      case '\t' => sb.append("\\t")
      case c if c < ' ' =>
        sb.append("\\u%04x".format(c.toInt))
      case c => sb.append(c)
    sb.toString

object Json:
  def obj(fields: (String, Json)*): Json = JObj(fields.toList)
  def arr(values: Json*): Json = JArr(values.toList)
  def str(s: String): Json = JStr(s)
  def num(n: Double): Json = JNum(n)
  def bool(b: Boolean): Json = JBool(b)
  val nullValue: Json = JNull

  /** Parse a JSON string. Throws on invalid input. */
  def parse(input: String): Json =
    val p = JsonParser(input)
    val result = p.parseValue()
    p.skipWhitespace()
    if p.pos < input.length then
      throw IllegalArgumentException(s"Unexpected trailing content at position ${p.pos}")
    result

private class JsonParser(input: String):
  var pos: Int = 0

  def skipWhitespace(): Unit =
    while pos < input.length && input(pos).isWhitespace do pos += 1

  def peek: Char =
    if pos >= input.length then
      throw IllegalArgumentException("Unexpected end of JSON input")
    input(pos)

  def advance(): Char =
    val c = peek
    pos += 1
    c

  def expect(c: Char): Unit =
    val got = advance()
    if got != c then
      throw IllegalArgumentException(s"Expected '$c' but got '$got' at position ${pos - 1}")

  def parseValue(): Json =
    skipWhitespace()
    peek match
      case '"' => parseString()
      case '{' => parseObject()
      case '[' => parseArray()
      case 't' | 'f' => parseBool()
      case 'n' => parseNull()
      case c if c == '-' || c.isDigit => parseNumber()
      case c => throw IllegalArgumentException(s"Unexpected character '$c' at position $pos")

  def parseString(): Json.JStr =
    expect('"')
    Json.JStr(readStringContent())

  def readStringContent(): String =
    val sb = StringBuilder()
    while peek != '"' do
      val c = advance()
      if c == '\\' then
        advance() match
          case '"'  => sb.append('"')
          case '\\' => sb.append('\\')
          case '/'  => sb.append('/')
          case 'b'  => sb.append('\b')
          case 'f'  => sb.append('\f')
          case 'n'  => sb.append('\n')
          case 'r'  => sb.append('\r')
          case 't'  => sb.append('\t')
          case 'u'  =>
            val hex = input.substring(pos, pos + 4)
            pos += 4
            sb.append(Integer.parseInt(hex, 16).toChar)
          case e => throw IllegalArgumentException(s"Invalid escape \\$e")
      else sb.append(c)
    expect('"')
    sb.toString

  def parseNumber(): Json.JNum =
    val start = pos
    if peek == '-' then pos += 1
    while pos < input.length && input(pos).isDigit do pos += 1
    if pos < input.length && input(pos) == '.' then
      pos += 1
      while pos < input.length && input(pos).isDigit do pos += 1
    if pos < input.length && (input(pos) == 'e' || input(pos) == 'E') then
      pos += 1
      if pos < input.length && (input(pos) == '+' || input(pos) == '-') then pos += 1
      while pos < input.length && input(pos).isDigit do pos += 1
    Json.JNum(input.substring(start, pos).toDouble)

  def parseBool(): Json.JBool =
    if input.startsWith("true", pos) then
      pos += 4; Json.JBool(true)
    else if input.startsWith("false", pos) then
      pos += 5; Json.JBool(false)
    else throw IllegalArgumentException(s"Invalid boolean at position $pos")

  def parseNull(): Json =
    if input.startsWith("null", pos) then
      pos += 4; Json.JNull
    else throw IllegalArgumentException(s"Invalid null at position $pos")

  def parseObject(): Json.JObj =
    expect('{')
    skipWhitespace()
    if peek == '}' then
      advance()
      return Json.JObj(Nil)
    val fields = scala.collection.mutable.ListBuffer[(String, Json)]()
    while true do
      skipWhitespace()
      expect('"')
      val key = readStringContent()
      skipWhitespace()
      expect(':')
      val value = parseValue()
      fields += ((key, value))
      skipWhitespace()
      if peek == ',' then advance()
      else if peek == '}' then
        advance()
        return Json.JObj(fields.toList)
      else throw IllegalArgumentException(s"Expected ',' or '}}' at position $pos")
    Json.JObj(fields.toList) // unreachable

  def parseArray(): Json.JArr =
    expect('[')
    skipWhitespace()
    if peek == ']' then
      advance()
      return Json.JArr(Nil)
    val values = scala.collection.mutable.ListBuffer[Json]()
    while true do
      values += parseValue()
      skipWhitespace()
      if peek == ',' then advance()
      else if peek == ']' then
        advance()
        return Json.JArr(values.toList)
      else throw IllegalArgumentException(s"Expected ',' or ']' at position $pos")
    Json.JArr(values.toList) // unreachable

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
