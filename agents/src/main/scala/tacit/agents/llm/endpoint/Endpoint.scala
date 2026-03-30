package tacit.agents
package llm.endpoint

import tacit.agents.utils.Result

case class EndpointConfig(
  baseUrl: String,
  apiKey: String,
):
  override def toString: String = s"EndpointConfig($baseUrl, ***)"

case class LLMConfig(
  model: String,
  systemPrompt: Option[String] = None,
  temperature: Option[Double] = None,
  maxTokens: Option[Int] = None,
  stopSequences: List[String] = List.empty,
  topP: Option[Double] = None,
  tools: List[Tool] = List.empty,
)

case class LLMError(description: String):
  override def toString: String = s"Error when invoking LLM: $description"

trait Endpoint:
  def invoke(messages: List[Message], config: LLMConfig): Result[ChatResponse, LLMError]

trait EndpointProvider:
  type EndpointType <: Endpoint
  def create(config: EndpointConfig): EndpointType
  def createFromEnv(): EndpointType
