package tacit.agents
package llm.endpoint

import gears.async.Async
import gears.async.default.given
import gears.async.Channel

class EndpointNetworkSuite extends munit.FunSuite:

  /** Read all items from a channel until it is closed. */
  private def readAll[T](ch: gears.async.ReadableChannel[T])(using Async): List[T] =
    val buf = scala.collection.mutable.ListBuffer[T]()
    var reading = true
    while reading do
      ch.read() match
        case Right(item) => buf += item
        case Left(_)     => reading = false
    buf.toList

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
    val config = LLMConfig(model = "claude-haiku-4-5", maxTokens = Some(16))
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
      model = "claude-haiku-4-5",
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
    val config = LLMConfig(model = "claude-haiku-4-5", maxTokens = Some(16))
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

  // Streaming tests: text

  test("OpenAIEndpoint.stream text response".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    Async.blocking:
      val endpoint = OpenAIEndpoint.createFromEnv()
      val config = LLMConfig(model = "gpt-4o-mini", maxTokens = Some(32))
      val ch = endpoint.stream(List(Message.user("Say hello")), config)
      val collected = readAll(ch)
      assert(collected.forall(_.isRight), s"Expected all Right but got errors")
      val streamEvents = collected.map(_.toOption.get)
      val deltas = streamEvents.collect { case StreamEvent.Delta(t) => t }
      assert(deltas.nonEmpty, "Expected at least one Delta event")
      val done = streamEvents.collectFirst { case d: StreamEvent.Done => d }
      assert(done.isDefined, "Expected Done event")
      val text = deltas.mkString
      assert(text.nonEmpty)
      assert(done.get.response.message.text == text)

  test("AnthropicEndpoint.stream text response".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    Async.blocking:
      val endpoint = AnthropicEndpoint.createFromEnv()
      val config = LLMConfig(model = "claude-haiku-4-5", maxTokens = Some(32))
      val ch = endpoint.stream(List(Message.user("Say hello")), config)
      val collected = readAll(ch)
      assert(collected.forall(_.isRight), s"Expected all Right but got errors")
      val streamEvents = collected.map(_.toOption.get)
      val deltas = streamEvents.collect { case StreamEvent.Delta(t) => t }
      assert(deltas.nonEmpty, "Expected at least one Delta event")
      val done = streamEvents.collectFirst { case d: StreamEvent.Done => d }
      assert(done.isDefined, "Expected Done event")
      val text = deltas.mkString
      assert(text.nonEmpty)
      assert(done.get.response.message.text == text)

  // Streaming tests: tool calling

  test("OpenAIEndpoint.stream with tool calling".tag(Network)):
    assume(sys.env.contains("OPENAI_API_KEY"), "OPENAI_API_KEY not set")
    Async.blocking:
      val endpoint = OpenAIEndpoint.createFromEnv()
      val config = LLMConfig(
        model = "gpt-4o-mini",
        maxTokens = Some(64),
        tools = List(weatherTool),
      )
      val ch = endpoint.stream(List(Message.user("What is the weather in Paris?")), config)
      val collected = readAll(ch)
      assert(collected.forall(_.isRight), s"Expected all Right but got ${collected.filter(_.isLeft)}")
      val streamEvents = collected.map(_.toOption.get)
      val starts = streamEvents.collect { case s: StreamEvent.ToolCallStart => s }
      assert(starts.nonEmpty, "Expected at least one ToolCallStart event")
      assert(starts.head.name == "get_weather")
      assert(starts.head.id.nonEmpty)
      val toolDeltas = streamEvents.collect { case d: StreamEvent.ToolCallDelta => d }
      assert(toolDeltas.nonEmpty, "Expected at least one ToolCallDelta event")
      val fullArgs = toolDeltas.map(_.argumentDelta).mkString
      assert(fullArgs.contains("Paris"), s"Expected tool args to contain Paris but got: $fullArgs")
      val done = streamEvents.collectFirst { case d: StreamEvent.Done => d }
      assert(done.isDefined, "Expected Done event")
      assert(done.get.response.finishReason == FinishReason.ToolUse)
      val toolUses = done.get.response.message.content.collect { case c: Content.ToolUse => c }
      assert(toolUses.nonEmpty)
      assert(toolUses.head.name == "get_weather")
      assert(toolUses.head.input.contains("Paris"))

  test("AnthropicEndpoint.stream with tool calling".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    Async.blocking:
      val endpoint = AnthropicEndpoint.createFromEnv()
      val config = LLMConfig(
        model = "claude-haiku-4-5",
        maxTokens = Some(256),
        tools = List(weatherTool),
        systemPrompt = Some("Use the provided tools to answer questions. Do not respond with text, just call the appropriate tool."),
      )
      val ch = endpoint.stream(List(Message.user("What is the weather in Paris?")), config)
      val collected = readAll(ch)
      assert(collected.forall(_.isRight), s"Expected all Right but got ${collected.filter(_.isLeft)}")
      val streamEvents = collected.map(_.toOption.get)
      val starts = streamEvents.collect { case s: StreamEvent.ToolCallStart => s }
      assert(starts.nonEmpty, "Expected at least one ToolCallStart event")
      assert(starts.head.name == "get_weather")
      assert(starts.head.id.nonEmpty)
      val toolDeltas = streamEvents.collect { case d: StreamEvent.ToolCallDelta => d }
      assert(toolDeltas.nonEmpty, "Expected at least one ToolCallDelta event")
      val fullArgs = toolDeltas.filter(_.index == starts.head.index).map(_.argumentDelta).mkString
      assert(fullArgs.contains("Paris"), s"Expected tool args to contain Paris but got: $fullArgs")
      val done = streamEvents.collectFirst { case d: StreamEvent.Done => d }
      assert(done.isDefined, "Expected Done event")
      assert(done.get.response.finishReason == FinishReason.ToolUse)
      val toolUses = done.get.response.message.content.collect { case c: Content.ToolUse => c }
      assert(toolUses.nonEmpty)
      assert(toolUses.head.name == "get_weather")
      assert(toolUses.head.input.contains("Paris"))

  // Non-streaming tool calling tests

  test("AnthropicEndpoint.invoke with tool calling".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "claude-haiku-4-5",
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

  // Thinking tests

  test("AnthropicEndpoint.invoke with thinking".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    val endpoint = AnthropicEndpoint.createFromEnv()
    val config = LLMConfig(
      model = "claude-haiku-4-5",
      maxTokens = Some(2048),
      thinking = Some(ThinkingMode.Budget(1024)),
    )
    val result = endpoint.invoke(List(Message.user("What is 17 * 23?")), config)
    assert(result.isRight, s"Expected Right but got $result")
    val response = result.toOption.get
    assert(response.message.thinking.nonEmpty, "Expected thinking content")
    assert(response.message.text.nonEmpty, "Expected text content")

  test("AnthropicEndpoint.stream with thinking".tag(Network)):
    assume(sys.env.contains("ANTHROPIC_API_KEY"), "ANTHROPIC_API_KEY not set")
    Async.blocking:
      val endpoint = AnthropicEndpoint.createFromEnv()
      val config = LLMConfig(
        model = "claude-haiku-4-5",
        maxTokens = Some(2048),
        thinking = Some(ThinkingMode.Budget(1024)),
      )
      val ch = endpoint.stream(List(Message.user("What is 17 * 23?")), config)
      val collected = readAll(ch)
      assert(collected.forall(_.isRight), s"Expected all Right but got ${collected.filter(_.isLeft)}")
      val streamEvents = collected.map(_.toOption.get)
      val thinkingDeltas = streamEvents.collect { case StreamEvent.ThinkingDelta(t) => t }
      assert(thinkingDeltas.nonEmpty, "Expected ThinkingDelta events")
      val textDeltas = streamEvents.collect { case StreamEvent.Delta(t) => t }
      assert(textDeltas.nonEmpty, "Expected text Delta events")
      val done = streamEvents.collectFirst { case d: StreamEvent.Done => d }
      assert(done.isDefined, "Expected Done event")
      assert(done.get.response.message.thinking == thinkingDeltas.mkString)
      assert(done.get.response.message.text == textDeltas.mkString)
