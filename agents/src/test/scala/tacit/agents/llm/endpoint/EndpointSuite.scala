package tacit.agents
package llm.endpoint

class EndpointSuite extends munit.FunSuite:

  test("OpenAIEndpoint.create with explicit config"):
    val config = EndpointConfig(baseUrl = "https://api.openai.com/v1", apiKey = "test-key")
    val endpoint = OpenAIEndpoint.create(config)
    assert(endpoint.isInstanceOf[OpenAIEndpoint])

  test("OpenAICompletionEndpoint.create with explicit config"):
    val config = EndpointConfig(baseUrl = "https://api.openai.com/v1", apiKey = "test-key")
    val endpoint = OpenAICompletionEndpoint.create(config)
    assert(endpoint.isInstanceOf[OpenAICompletionEndpoint])

  test("AnthropicEndpoint.create with explicit config"):
    val config = EndpointConfig(baseUrl = "https://api.anthropic.com", apiKey = "test-key")
    val endpoint = AnthropicEndpoint.create(config)
    assert(endpoint.isInstanceOf[AnthropicEndpoint])

  // Message factory tests

  test("Message.user creates User message with Text content"):
    val msg = Message.user("hello")
    assert(msg.role == Role.User)
    assert(msg.content == List(Content.Text("hello")))

  test("Message.assistant creates Assistant message with Text content"):
    val msg = Message.assistant("hi there")
    assert(msg.role == Role.Assistant)
    assert(msg.content == List(Content.Text("hi there")))

  test("Message.system creates System message with Text content"):
    val msg = Message.system("you are helpful")
    assert(msg.role == Role.System)
    assert(msg.content == List(Content.Text("you are helpful")))

  test("Message.toolResult creates User message with ToolResult content"):
    val msg = Message.toolResult("call-123", """{"temp": 20}""")
    assert(msg.role == Role.User)
    msg.content.head match
      case Content.ToolResult(id, content, isError) =>
        assert(id == "call-123")
        assert(content == """{"temp": 20}""")
        assert(!isError)
      case other => fail(s"Expected ToolResult but got $other")

  test("Message.toolResult with isError flag"):
    val msg = Message.toolResult("call-456", "something went wrong", isError = true)
    msg.content.head match
      case Content.ToolResult(_, _, isError) => assert(isError)
      case other => fail(s"Expected ToolResult but got $other")

  // Message accessor tests

  test("Message.text extracts and concatenates Text content"):
    val msg = Message(Role.Assistant, List(
      Content.Thinking("let me think"),
      Content.Text("hello "),
      Content.ToolUse("id", "fn", "{}"),
      Content.Text("world"),
    ))
    assert(msg.text == "hello world")

  test("Message.thinking extracts and concatenates Thinking content"):
    val msg = Message(Role.Assistant, List(
      Content.Thinking("step 1, "),
      Content.Text("answer"),
      Content.Thinking("step 2"),
    ))
    assert(msg.thinking == "step 1, step 2")

  test("Message.text returns empty string when no Text content"):
    val msg = Message(Role.Assistant, List(Content.Thinking("thinking only")))
    assert(msg.text == "")

  test("Message.thinking returns empty string when no Thinking content"):
    val msg = Message(Role.User, List(Content.Text("hello")))
    assert(msg.thinking == "")

  // LLMConfig defaults

  test("LLMConfig has correct defaults"):
    val config = LLMConfig(model = "test-model")
    assert(config.systemPrompt.isEmpty)
    assert(config.temperature.isEmpty)
    assert(config.maxTokens.isEmpty)
    assert(config.stopSequences.isEmpty)
    assert(config.topP.isEmpty)
    assert(config.tools.isEmpty)
    assert(config.thinking.isEmpty)

  // ThinkingMode error case tests

  test("OpenAIEndpoint.invoke with ThinkingMode.Budget returns error"):
    val endpoint = OpenAIEndpoint.create(EndpointConfig("https://api.openai.com/v1", "dummy"))
    val config = LLMConfig(
      model = "o4-mini",
      maxTokens = Some(16),
      thinking = Some(ThinkingMode.Budget(1024)),
    )
    val result = endpoint.invoke(List(Message.user("hello")), config)
    assert(result.isLeft)
    assert(result.left.toOption.get.description.contains("Budget"))

  test("OpenAICompletionEndpoint.invoke with ThinkingMode.Budget returns error"):
    val endpoint = OpenAICompletionEndpoint.create(EndpointConfig("https://api.openai.com/v1", "dummy"))
    val config = LLMConfig(
      model = "o4-mini",
      maxTokens = Some(16),
      thinking = Some(ThinkingMode.Budget(1024)),
    )
    val result = endpoint.invoke(List(Message.user("hello")), config)
    assert(result.isLeft)
    assert(result.left.toOption.get.description.contains("Budget"))

  test("AnthropicEndpoint.invoke with ThinkingMode.Effort returns error"):
    val endpoint = AnthropicEndpoint.create(EndpointConfig("https://api.anthropic.com", "dummy"))
    val config = LLMConfig(
      model = "claude-haiku-4-5",
      maxTokens = Some(16),
      thinking = Some(ThinkingMode.Effort(EffortLevel.Low)),
    )
    val result = endpoint.invoke(List(Message.user("hello")), config)
    assert(result.isLeft)
    assert(result.left.toOption.get.description.contains("Effort"))
