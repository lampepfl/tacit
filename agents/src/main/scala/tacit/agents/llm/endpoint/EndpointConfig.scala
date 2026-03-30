package tacit.agents
package llm.endpoint

case class EndpointConfig(
  baseUrl: String,
  apiKey: String,
):
  override def toString: String = s"EndpointConfig($baseUrl, ***)"
