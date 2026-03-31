package tacit.agents
package llm.endpoint

import gears.async.Async
import gears.async.ReadableChannel
import tacit.agents.utils.Result

case class EndpointConfig(
  baseUrl: String,
  apiKey: String,
):
  override def toString: String = s"EndpointConfig($baseUrl, ***)"

enum EffortLevel:
  case Low, Medium, High, XHigh

enum ThinkingMode:
  case Disabled
  case Auto
  case Budget(tokens: Int)
  case Effort(level: EffortLevel)

case class LLMConfig(
  model: String,
  systemPrompt: Option[String] = None,
  temperature: Option[Double] = None,
  maxTokens: Option[Int] = None,
  stopSequences: List[String] = List.empty,
  topP: Option[Double] = None,
  tools: List[ToolSchema] = List.empty,
  thinking: Option[ThinkingMode] = None,
)

class LLMError(val description: String):
  override def toString: String = s"Error when invoking LLM: $description"

trait Endpoint:
  def invoke(messages: List[Message], config: LLMConfig): Result[ChatResponse, LLMError]
  def stream(messages: List[Message], config: LLMConfig)(using Async.Spawn): ReadableChannel[Result[StreamEvent, LLMError]]

trait EndpointProvider:
  type EndpointType <: Endpoint
  def create(config: EndpointConfig): EndpointType
  def createFromEnv(): EndpointType
