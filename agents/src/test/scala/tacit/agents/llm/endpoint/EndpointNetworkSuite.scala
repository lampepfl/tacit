package tacit.agents
package llm.endpoint

class EndpointNetworkSuite extends munit.FunSuite:

  test("OpenAIEndpoint.createFromEnv reads OPENAI_API_KEY".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    val endpoint = OpenAIEndpoint.createFromEnv()
    assert(endpoint.isInstanceOf[OpenAIEndpoint])

  test("AnthropicEndpoint.createFromEnv reads ANTHROPIC_API_KEY".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    assert(endpoint.isInstanceOf[AnthropicEndpoint])

  test("OpenAIEndpoint.invoke returns a response".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    val endpoint = OpenAIEndpoint.createFromEnv()
    val config = LLMConfig(model = "gpt-4o-mini", maxTokens = Some(16))
    val result = endpoint.invoke(List(Message.user("Say hello")), config)
    assert(result.isRight, s"Expected Right but got $result")
    val response = result.toOption.get
    assert(response.message.role == Role.Assistant)
    assert(response.message.text.nonEmpty)
    assert(response.finishReason == FinishReason.Stop || response.finishReason == FinishReason.MaxTokens)

  test("OpenAIEndpoint.invoke with system prompt".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    val endpoint = OpenAIEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "gpt-4o-mini",
      maxTokens = Some(16),
      systemPrompt = Some("You are a helpful assistant. Reply only with the word PONG."),
    )
    val result = endpoint.invoke(List(Message.user("PING")), config)
    assert(result.isRight, s"Expected Right but got $result")
    assert(result.toOption.get.message.text.contains("PONG"))

  test("OpenAIEndpoint.invoke with invalid key returns Left".tag(Network)):
    val endpoint = OpenAIEndpoint.create(EndpointConfig(
      baseUrl = "https://api.openai.com/v1",
      apiKey = "sk-invalid",
    ))
    val config = LLMConfig(model = "gpt-4o-mini", maxTokens = Some(16))
    val result = endpoint.invoke(List(Message.user("hello")), config)
    assert(result.isLeft)

  test("AnthropicEndpoint.invoke returns a response".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    val config = LLMConfig(model = "claude-sonnet-4-20250514", maxTokens = Some(16))
    val result = endpoint.invoke(List(Message.user("Say hello")), config)
    assert(result.isRight, s"Expected Right but got $result")
    val response = result.toOption.get
    assert(response.message.role == Role.Assistant)
    assert(response.message.text.nonEmpty)
    assert(response.finishReason == FinishReason.Stop || response.finishReason == FinishReason.MaxTokens)
    assert(response.usage.isDefined)

  test("AnthropicEndpoint.invoke with system prompt".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "claude-sonnet-4-20250514",
      maxTokens = Some(16),
      systemPrompt = Some("You are a helpful assistant. Reply only with the word PONG."),
    )
    val result = endpoint.invoke(List(Message.user("PING")), config)
    assert(result.isRight, s"Expected Right but got $result")
    assert(result.toOption.get.message.text.contains("PONG"))

  test("AnthropicEndpoint.invoke with invalid key returns Left".tag(Network)):
    val endpoint = AnthropicEndpoint.create(EndpointConfig(
      baseUrl = "https://api.anthropic.com",
      apiKey = "sk-ant-invalid",
    ))
    val config = LLMConfig(model = "claude-sonnet-4-20250514", maxTokens = Some(16))
    val result = endpoint.invoke(List(Message.user("hello")), config)
    assert(result.isLeft)

  private val weatherTool = ToolSchema(
    name = "get_weather",
    description = "Get the current weather in a given location",
    parameters = ToolSchema.Parameters(
      properties = Map(
        "location" -> ToolSchema.Property(`type` = "string", description = "The city name"),
      ),
      required = List("location"),
    ),
  )

  test("OpenAIEndpoint.invoke with tool calling".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    val endpoint = OpenAIEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "gpt-4o-mini",
      maxTokens = Some(64),
      tools = List(weatherTool),
    )
    val result = endpoint.invoke(List(Message.user("What is the weather in Paris?")), config)
    assert(result.isRight, s"Expected Right but got $result")
    val response = result.toOption.get
    assert(response.finishReason == FinishReason.ToolUse)
    val toolUses = response.message.content.collect { case c: Content.ToolUse => c }
    assert(toolUses.nonEmpty, "Expected at least one tool use")
    assert(toolUses.head.name == "get_weather")
    assert(toolUses.head.input.contains("Paris"))

  test("AnthropicEndpoint.invoke with tool calling".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "claude-sonnet-4-20250514",
      maxTokens = Some(256),
      tools = List(weatherTool),
      systemPrompt = Some("Use the provided tools to answer questions. Do not respond with text, just call the appropriate tool."),
    )
    val result = endpoint.invoke(List(Message.user("What is the weather in Paris?")), config)
    assert(result.isRight, s"Expected Right but got $result")
    val response = result.toOption.get
    assert(response.finishReason == FinishReason.ToolUse)
    val toolUses = response.message.content.collect { case c: Content.ToolUse => c }
    assert(toolUses.nonEmpty, "Expected at least one tool use")
    assert(toolUses.head.name == "get_weather")
    assert(toolUses.head.input.contains("Paris"))
