package tacit.library.banking

import language.experimental.captureChecking
import java.net.URI
import java.net.http.{HttpClient, HttpRequest, HttpResponse}

class MCPError(message: String) extends Exception(message)

class MCPClient(baseUrl: String) extends AutoCloseable:
  private val httpClient = HttpClient.newHttpClient().nn
  private var sessionId: Option[String] = None
  private var nextId: Int = 1

  private def allocateId(): Int =
    val id = nextId
    nextId += 1
    id

  def sendRequest(request: JsonRpcRequest): List[JsonRpcResponse] =
    val body = request.toJson.compact
    val builder = HttpRequest.newBuilder()
      .nn.uri(URI.create(baseUrl))
      .nn.header("Content-Type", "application/json")
      .nn.header("Accept", "application/json, text/event-stream")
      .nn.POST(HttpRequest.BodyPublishers.ofString(body))
      .nn
    sessionId.foreach(sid => builder.header("Mcp-Session-Id", sid))
    val httpReq = builder.build().nn
    val httpResp = httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString()).nn
    val status = httpResp.statusCode()
    if status < 200 || status >= 300 then
      throw MCPError(s"HTTP $status: ${httpResp.body()}")

    // Track session ID
    val sidOpt = httpResp.headers().nn.firstValue("mcp-session-id").nn
    if sidOpt.isPresent then sessionId = Some(sidOpt.get().nn)

    val contentType = httpResp.headers().nn.firstValue("content-type").nn
    val respBody = httpResp.body().nn

    if contentType.isPresent && contentType.get().nn.startsWith("text/event-stream") then
      parseSse(respBody)
    else
      List(JsonRpcResponse.fromJson(JValue.parse(respBody)))

  def sendNotification(method: String, params: Option[JValue] = None): Unit =
    val req = JsonRpcRequest.notification(method, params)
    val body = req.toJson.compact
    val builder = HttpRequest.newBuilder()
      .nn.uri(URI.create(baseUrl))
      .nn.header("Content-Type", "application/json")
      .nn.header("Accept", "application/json, text/event-stream")
      .nn.POST(HttpRequest.BodyPublishers.ofString(body))
      .nn
    sessionId.foreach(sid => builder.header("Mcp-Session-Id", sid))
    val httpReq = builder.build().nn
    httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString())

  def initialize(): JValue =
    val params = JValue.obj(
      "protocolVersion" -> JValue.str("2025-11-25"),
      "capabilities" -> JValue.obj(),
      "clientInfo" -> JValue.obj(
        "name" -> JValue.str("tacit-banking-client"),
        "version" -> JValue.str("0.1.0")
      )
    )
    val req = JsonRpcRequest.request("initialize", params, allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def sendInitialized(): Unit =
    sendNotification("notifications/initialized")

  def listTools(): JValue =
    val req = JsonRpcRequest.request("tools/list", JValue.obj(), allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def callTool(name: String, arguments: JValue): JValue =
    val params = JValue.obj(
      "name" -> JValue.str(name),
      "arguments" -> arguments
    )
    val req = JsonRpcRequest.request("tools/call", params, allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def close(): Unit = ()

  private def extractResult(responses: List[JsonRpcResponse]): JValue =
    val resp = responses.lastOption.getOrElse(
      throw MCPError("No response received")
    )
    resp.error.foreach { err =>
      throw MCPError(s"JSON-RPC error ${err.code}: ${err.message}")
    }
    resp.result.getOrElse(JValue.JNull)

  private def parseSse(body: String): List[JsonRpcResponse] =
    body.linesIterator
      .filter(_.startsWith("data: "))
      .map(line => JsonRpcResponse.fromJson(JValue.parse(line.drop(6))))
      .toList
