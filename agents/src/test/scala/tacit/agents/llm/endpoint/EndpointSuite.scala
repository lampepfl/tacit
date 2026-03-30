package tacit.agents
package llm.endpoint

class EndpointSuite extends munit.FunSuite:

  test("OpenAIEndpoint.create with explicit config"):
    val config = EndpointConfig(baseUrl = "https://api.openai.com/v1", apiKey = "test-key")
    val endpoint = OpenAIEndpoint.create(config)
    assert(endpoint.isInstanceOf[OpenAIEndpoint])

  test("AnthropicEndpoint.create with explicit config"):
    val config = EndpointConfig(baseUrl = "https://api.anthropic.com", apiKey = "test-key")
    val endpoint = AnthropicEndpoint.create(config)
    assert(endpoint.isInstanceOf[AnthropicEndpoint])
