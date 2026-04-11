package tacit.library.banking.mcp

import io.circe.*
import io.circe.syntax.*
import io.circe.parser.decode
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
    val body = request.asJson.noSpaces
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
      decode[JsonRpcResponse](respBody) match
        case Right(resp) => List(resp)
        case Left(err) => throw MCPError(s"Failed to parse response: $err")

  def sendNotification(method: String, params: Option[Json] = None): Unit =
    val req = JsonRpcRequest.notification(method, params)
    val body = req.asJson.noSpaces
    val builder = HttpRequest.newBuilder()
      .nn.uri(URI.create(baseUrl))
      .nn.header("Content-Type", "application/json")
      .nn.header("Accept", "application/json, text/event-stream")
      .nn.POST(HttpRequest.BodyPublishers.ofString(body))
      .nn
    sessionId.foreach(sid => builder.header("Mcp-Session-Id", sid))
    val httpReq = builder.build().nn
    httpClient.send(httpReq, HttpResponse.BodyHandlers.ofString())

  def initialize(): Json =
    val params = Json.obj(
      "protocolVersion" -> Json.fromString("2025-11-25"),
      "capabilities" -> Json.obj(),
      "clientInfo" -> Json.obj(
        "name" -> Json.fromString("tacit-banking-client"),
        "version" -> Json.fromString("0.1.0")
      )
    )
    val req = JsonRpcRequest.request("initialize", params, allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def sendInitialized(): Unit =
    sendNotification("notifications/initialized")

  def listTools(): Json =
    val req = JsonRpcRequest.request("tools/list", Json.obj(), allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def callTool(name: String, arguments: Json): Json =
    val params = Json.obj(
      "name" -> Json.fromString(name),
      "arguments" -> arguments
    )
    val req = JsonRpcRequest.request("tools/call", params, allocateId())
    val responses = sendRequest(req)
    extractResult(responses)

  def close(): Unit = ()

  private def extractResult(responses: List[JsonRpcResponse]): Json =
    val resp = responses.lastOption.getOrElse(
      throw MCPError("No response received")
    )
    resp.error.foreach { err =>
      throw MCPError(s"JSON-RPC error ${err.code}: ${err.message}")
    }
    resp.result.getOrElse(Json.Null)

  private def parseSse(body: String): List[JsonRpcResponse] =
    body.linesIterator
      .filter(_.startsWith("data: "))
      .map(_.drop(6))
      .flatMap(line => decode[JsonRpcResponse](line).toOption)
      .toList
