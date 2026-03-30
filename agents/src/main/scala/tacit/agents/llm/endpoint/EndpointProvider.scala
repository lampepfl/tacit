package tacit.agents
package llm.endpoint

trait EndpointProvider:
  type EndpointType <: Endpoint
  def create(config: EndpointConfig): EndpointType
  def createFromEnv(): EndpointType
