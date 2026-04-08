package tacit.agents
package llm.endpoint

/** Ollama endpoint factory.
  *
  * Ollama exposes an OpenAI Responses API-compatible endpoint, so this
  * delegates entirely to [[OpenAIEndpoint]] with the appropriate base URL
  * and API key.
  */
object OllamaEndpoint extends EndpointProvider:
  type EndpointType = OpenAIEndpoint

  override def create(config: EndpointConfig): OpenAIEndpoint =
    OpenAIEndpoint.create(config)

  override def createFromEnv(): OpenAIEndpoint =
    val apiKey = sys.env.getOrElse("OLLAMA_API_KEY", "ollama")
    val baseUrl = sys.env.getOrElse("OLLAMA_BASE_URL", "http://localhost:11434/v1")
    create(EndpointConfig(baseUrl = baseUrl, apiKey = apiKey))
