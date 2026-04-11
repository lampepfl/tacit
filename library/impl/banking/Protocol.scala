package tacit.library.banking

import io.circe.*
import io.circe.generic.semiauto.*

case class JsonRpcRequest(
    jsonrpc: String = "2.0",
    method: String,
    params: Option[Json] = None,
    id: Option[Json] = None
)

object JsonRpcRequest:
  given Encoder[JsonRpcRequest] = deriveEncoder[JsonRpcRequest].mapJson(_.dropNullValues)

  def request(method: String, params: Json, id: Int): JsonRpcRequest =
    JsonRpcRequest(method = method, params = Some(params), id = Some(Json.fromInt(id)))

  def notification(method: String, params: Option[Json] = None): JsonRpcRequest =
    JsonRpcRequest(method = method, params = params)

case class JsonRpcResponse(
    jsonrpc: String,
    result: Option[Json] = None,
    error: Option[JsonRpcError] = None,
    id: Option[Json] = None
)

object JsonRpcResponse:
  given Decoder[JsonRpcResponse] = deriveDecoder

case class JsonRpcError(
    code: Int,
    message: String,
    data: Option[Json] = None
)

object JsonRpcError:
  given Decoder[JsonRpcError] = deriveDecoder
