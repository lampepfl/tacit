package tacit.agents
package llm
package agentic

import endpoint.{Endpoint, LLMConfig, LLMError, Message, Content, ChatResponse, FinishReason, Usage}
import tacit.agents.utils.Result
import tacit.agents.llm.utils.IsToolArg
import gears.async.{Async, ReadableChannel}
import endpoint.StreamEvent

// --- Stub Endpoint ---

class StubEndpoint(responses: List[ChatResponse]) extends Endpoint:
  private var callIndex = 0
  private var _invokedWith: List[List[Message]] = Nil

  def invokedWith: List[List[Message]] = _invokedWith

  def invoke(messages: List[Message], config: LLMConfig): Result[ChatResponse, LLMError] =
    _invokedWith = _invokedWith :+ messages
    if callIndex < responses.length then
      val resp = responses(callIndex)
      callIndex += 1
      Right(resp)
    else
      Left(LLMError("No more stub responses"))

  def stream(messages: List[Message], config: LLMConfig)(using Async.Spawn): ReadableChannel[Result[StreamEvent, LLMError]] =
    throw UnsupportedOperationException("StubEndpoint does not support streaming")

// --- Test tool definitions ---

case class CalcArgs(expression: String) derives IsToolArg

object CalcTool extends AgentTool[AgentState]:
  type ArgType = CalcArgs
  def name = "calculate"
  def description = "Evaluate a math expression"
  def handle(arg: CalcArgs, state: AgentState): String = s"Result: 42"

case class LookupArgs(key: String) derives IsToolArg

object LookupTool extends AgentTool[AgentState]:
  type ArgType = LookupArgs
  def name = "lookup"
  def description = "Look up a value by key"
  def handle(arg: LookupArgs, state: AgentState): String = s"Value for ${arg.key}: found"

case class GreetArgs(name: String) derives IsToolArg

// --- Test state ---

class SimpleState(val llmConfig: LLMConfig) extends AgentState

// --- Helpers ---

def textResponse(text: String): ChatResponse =
  ChatResponse(Message.assistant(text), FinishReason.Stop)

def toolCallResponse(calls: (String, String, String)*): ChatResponse =
  val content = calls.map: (id, name, input) =>
    Content.ToolUse(id, name, input)
  ChatResponse(Message(endpoint.Role.Assistant, content.toList), FinishReason.ToolUse)

val defaultConfig = LLMConfig(model = "test-model")

def makeAgent(tools: List[AgentTool[AgentState]] = Nil): Agent =
  val agent = new Agent:
    type State = SimpleState
    def getInitState = SimpleState(defaultConfig)
  agent.tools = tools
  agent

// --- Tests ---

class AgentSuite extends munit.FunSuite:

  test("ask: simple response with no tool calls"):
    val ep = StubEndpoint(List(textResponse("Hello!")))
    given Endpoint = ep
    val agent = makeAgent()
    val result = agent.ask("Hi")
    assert(result.isRight)
    assertEquals(result.toOption.get.message.text, "Hello!")

  test("ask: single tool call then final response"):
    val ep = StubEndpoint(List(
      toolCallResponse(("call-1", "calculate", """{"expression": "2+2"}""")),
      textResponse("The answer is 42."),
    ))
    given Endpoint = ep
    val agent = makeAgent(List(CalcTool))
    val result = agent.ask("What is 2+2?")
    assert(result.isRight)
    assertEquals(result.toOption.get.message.text, "The answer is 42.")
    assertEquals(ep.invokedWith.size, 2)
    val secondCall = ep.invokedWith(1)
    val toolResult = secondCall.last.content.collectFirst:
      case Content.ToolResult(id, content, _) => (id, content)
    assertEquals(toolResult, Some(("call-1", "Result: 42")))

  test("ask: multiple tool calls in one response"):
    val ep = StubEndpoint(List(
      toolCallResponse(
        ("call-1", "calculate", """{"expression": "1+1"}"""),
        ("call-2", "lookup", """{"key": "pi"}"""),
      ),
      textResponse("Done."),
    ))
    given Endpoint = ep
    val agent = makeAgent(List(CalcTool, LookupTool))
    val result = agent.ask("Do both")
    assert(result.isRight)
    assertEquals(result.toOption.get.message.text, "Done.")
    val secondCall = ep.invokedWith(1)
    val toolResults = secondCall.flatMap(_.content).collect:
      case Content.ToolResult(id, content, _) => (id, content)
    assertEquals(toolResults.size, 2)

  test("ask: unknown tool name returns error"):
    val ep = StubEndpoint(List(
      toolCallResponse(("call-1", "nonexistent", """{}""")),
    ))
    given Endpoint = ep
    val agent = makeAgent()
    val result = agent.ask("Call something")
    assert(result.isLeft)
    assert(result.swap.toOption.get.description.contains("Unknown tool"))

  test("ask: tool arg parse failure returns error"):
    val ep = StubEndpoint(List(
      toolCallResponse(("call-1", "calculate", """not json""")),
    ))
    given Endpoint = ep
    val agent = makeAgent(List(CalcTool))
    val result = agent.ask("Calculate")
    assert(result.isLeft)
    assert(result.swap.toOption.get.description.contains("Failed to parse args"))

  test("ask: endpoint error propagates"):
    val ep = StubEndpoint(Nil)
    given Endpoint = ep
    val agent = makeAgent()
    val result = agent.ask("Hi")
    assert(result.isLeft)
    assert(result.swap.toOption.get.description.contains("No more stub responses"))

  test("addTool: mutably adds tool and returns same agent"):
    val agent = makeAgent()
    assertEquals(agent.tools.size, 0)
    val same = agent.addTool(CalcTool)
    assert(same eq agent)
    assertEquals(agent.tools.size, 1)
    assertEquals(agent.tools.head.name, "calculate")

  test("addTool: chains multiple tools"):
    val agent = makeAgent()
    agent.addTool(CalcTool).addTool(LookupTool)
    assertEquals(agent.tools.size, 2)
    assertEquals(agent.tools.map(_.name), List("calculate", "lookup"))

  test("addTool: rejects duplicate tool name"):
    val agent = makeAgent()
    agent.addTool(CalcTool)
    intercept[IllegalArgumentException]:
      agent.addTool(CalcTool)

  test("addTools: adds multiple tools at once"):
    val agent = makeAgent()
    val same = agent.addTools(CalcTool, LookupTool)
    assert(same eq agent)
    assertEquals(agent.tools.size, 2)
    assertEquals(agent.tools.map(_.name), List("calculate", "lookup"))

  test("addTools: rejects duplicates among new tools"):
    val agent = makeAgent()
    intercept[IllegalArgumentException]:
      agent.addTools(CalcTool, CalcTool)

  test("handle: creates tool from lambda"):
    val ep = StubEndpoint(List(
      toolCallResponse(("call-1", "greet", """{"name": "Alice"}""")),
      textResponse("Done."),
    ))
    given Endpoint = ep
    val agent = makeAgent()
    agent.handle[GreetArgs]("greet", "Greet someone"): (arg, state) =>
      s"Hello, ${arg.name}!"
    assertEquals(agent.tools.size, 1)
    assertEquals(agent.tools.head.name, "greet")
    val result = agent.ask("Greet Alice")
    assert(result.isRight)
    val secondCall = ep.invokedWith(1)
    val toolResult = secondCall.last.content.collectFirst:
      case Content.ToolResult(_, content, _) => content
    assertEquals(toolResult, Some("Hello, Alice!"))

  test("handle: rejects duplicate name"):
    val agent = makeAgent()
    agent.handle[GreetArgs]("greet", "Greet someone")((arg, _) => "hi")
    intercept[IllegalArgumentException]:
      agent.handle[GreetArgs]("greet", "Greet again")((arg, _) => "hi")

  // --- state.messages update tests ---

  test("ask: updates state.messages with user and assistant messages"):
    val ep = StubEndpoint(List(textResponse("Hello!")))
    given Endpoint = ep
    val agent = makeAgent()
    assertEquals(agent.state.messages.size, 0)
    agent.ask("Hi")
    assertEquals(agent.state.messages.size, 2)
    assertEquals(agent.state.messages(0).text, "Hi")
    assertEquals(agent.state.messages(1).text, "Hello!")

  test("ask: state.messages includes tool use and tool result on tool calls"):
    val ep = StubEndpoint(List(
      toolCallResponse(("call-1", "calculate", """{"expression": "2+2"}""")),
      textResponse("The answer is 42."),
    ))
    given Endpoint = ep
    val agent = makeAgent(List(CalcTool))
    agent.ask("What is 2+2?")
    // user + assistant(tool_use) + tool_result + assistant(final)
    assertEquals(agent.state.messages.size, 4)
    val toolResult = agent.state.messages(2).content.collectFirst:
      case Content.ToolResult(id, content, _) => (id, content)
    assertEquals(toolResult, Some(("call-1", "Result: 42")))
    assertEquals(agent.state.messages(3).text, "The answer is 42.")

  test("ask: successive asks accumulate messages"):
    val ep = StubEndpoint(List(textResponse("First"), textResponse("Second")))
    given Endpoint = ep
    val agent = makeAgent()
    agent.ask("One")
    agent.ask("Two")
    assertEquals(agent.state.messages.size, 4)
    assertEquals(agent.state.messages.map(_.text), List("One", "First", "Two", "Second"))
