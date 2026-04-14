package tacit.library.mcp

import language.experimental.captureChecking

case class JsonRpcRequest(
    jsonrpc: String = "2.0",
    method: String,
    params: Option[JValue] = None,
    id: Option[Int] = None
):
  def toJson: JValue =
    val fields = List(
      "jsonrpc" -> JValue.str(jsonrpc),
      "method" -> JValue.str(method),
    ) ++ params.map("params" -> _).toList
      ++ id.map(i => "id" -> JValue.num(i)).toList
    JValue.JObject(fields)

object JsonRpcRequest:
  def request(method: String, params: JValue, id: Int): JsonRpcRequest =
    JsonRpcRequest(method = method, params = Some(params), id = Some(id))

  def notification(method: String, params: Option[JValue] = None): JsonRpcRequest =
    JsonRpcRequest(method = method, params = params)

case class JsonRpcResponse(
    result: Option[JValue],
    error: Option[JsonRpcError],
    id: Option[JValue]
)

object JsonRpcResponse:
  def fromJson(j: JValue): JsonRpcResponse =
    val error = j.field("error") match
      case JValue.JNull => None
      case e => Some(JsonRpcError.fromJson(e))
    val result = j.field("result") match
      case JValue.JNull => None
      case r => Some(r)
    val id = j.field("id") match
      case JValue.JNull => None
      case i => Some(i)
    JsonRpcResponse(result, error, id)

case class JsonRpcError(
    code: Int,
    message: String,
    data: Option[JValue] = None
)

object JsonRpcError:
  def fromJson(j: JValue): JsonRpcError =
    JsonRpcError(
      code = j.field("code").asInt.getOrElse(0),
      message = j.field("message").asString.getOrElse(""),
      data = j.field("data") match
        case JValue.JNull => None
        case d => Some(d)
    )
