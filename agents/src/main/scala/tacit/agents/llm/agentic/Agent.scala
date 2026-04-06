package tacit.agents
package llm
package agentic

import endpoint.{Endpoint, LLMConfig, LLMError, Message, Content, ChatResponse, FinishReason, ToolSchema, StreamEvent}
import tacit.agents.utils.Result
import tacit.agents.utils.Result.ok
import llm.utils.{IsToolArg, ToolArgParsingError}
import scala.util.boundary
import scala.annotation.tailrec
import gears.async.{Async, Future, BufferedChannel, ReadableChannel, Channel}

class AgentError(val description: String):
  override def toString: String = s"AgentError: $description"

enum AgentStreamEvent:
  case Stream(event: StreamEvent)
  case ToolResult(id: String, toolName: String, result: String)

trait AgentState:
  val llmConfig: LLMConfig
  var messages: List[Message] = Nil

trait AgentTool[-StateType <: AgentState]:
  type ArgType: IsToolArg

  def name: String
  def description: String

  def handle(arg: ArgType, state: StateType): String

  def toolSchema: ToolSchema =
    ToolSchema(name, description, parameters = summon[IsToolArg[ArgType]].schema)

  def parseArgs(input: String): Result[ArgType, ToolArgParsingError] =
    summon[IsToolArg[ArgType]].parse(input)

abstract class Agent:
  type State <: AgentState

  val state: State = getInitState
  var tools: List[AgentTool[State]] = Nil

  def getInitState: State

  def addTool(tool: AgentTool[State]): this.type =
    if tools.exists(_.name == tool.name) then
      throw IllegalArgumentException(s"Tool with name '${tool.name}' already exists")
    tools = tools :+ tool
    this

  def addTools(newTools: AgentTool[State]*): this.type =
    newTools.foreach(addTool)
    this

  def handle[A: IsToolArg](toolName: String, desc: String)(handler: (A, this.State) => String): this.type =
    val tool = new AgentTool[State]:
      type ArgType = A
      def name = toolName
      def description = desc
      def handle(arg: A, state: State): String = handler(arg, state)
    addTool(tool)

  def ask(message: String)(using endpoint: Endpoint): Result[ChatResponse, AgentError] =
    state.messages = state.messages :+ Message.user(message)
    val config = state.llmConfig.copy(tools = tools.map(_.toolSchema))
    Result:
      loop(config)

  @tailrec
  private def loop(config: LLMConfig)(using endpoint: Endpoint, label: boundary.Label[Result[ChatResponse, AgentError]]): ChatResponse =
    val response = endpoint.invoke(state.messages, config) match
      case Right(r) => r
      case Left(e) => boundary.break(Left(AgentError(e.description)))

    state.messages = state.messages :+ response.message

    response.finishReason match
      case FinishReason.ToolUse =>
        val toolUses = response.message.content.collect:
          case tu: Content.ToolUse => tu

        val toolResults = toolUses.map: tu =>
          dispatchTool(tu).ok

        state.messages = state.messages :++ toolResults
        loop(config)

      case _ => response

  def streamAsk(message: String)(using endpoint: Endpoint, spawn: Async.Spawn): ReadableChannel[Result[AgentStreamEvent, AgentError]] =
    state.messages = state.messages :+ Message.user(message)
    val config = state.llmConfig.copy(tools = tools.map(_.toolSchema))
    val ch = BufferedChannel[Result[AgentStreamEvent, AgentError]](16)
    Future:
      streamLoop(config, ch)(using endpoint)
    ch.asReadable

  private def streamLoop(config: LLMConfig, ch: BufferedChannel[Result[AgentStreamEvent, AgentError]])(using endpoint: Endpoint, spawn: Async.Spawn): Unit =
    try
      val streamCh = endpoint.stream(state.messages, config)
      val response = consumeStream(streamCh, ch)

      state.messages = state.messages :+ response.message

      response.finishReason match
        case FinishReason.ToolUse =>
          val toolUses = response.message.content.collect:
            case tu: Content.ToolUse => tu

          val dispatched = toolUses.map(tu => (tu, dispatchTool(tu)))
          val failed = dispatched.collectFirst { case (tu, Left(err)) => err }

          failed match
            case Some(err) =>
              ch.send(Left(err))
              ch.close()
            case None =>
              for case (tu, Right(msg)) <- dispatched do
                val resultContent = msg.content.collectFirst:
                  case Content.ToolResult(_, content, _) => content
                ch.send(Right(AgentStreamEvent.ToolResult(tu.id, tu.name, resultContent.getOrElse(""))))
                state.messages = state.messages :+ msg
              streamLoop(config, ch)

        case _ =>
          ch.close()
    catch
      case e: Exception =>
        ch.send(Left(AgentError(s"Stream error: ${e.getMessage}")))
        ch.close()

  private def consumeStream(
    streamCh: ReadableChannel[Result[StreamEvent, LLMError]],
    outCh: BufferedChannel[Result[AgentStreamEvent, AgentError]]
  )(using Async): ChatResponse =
    var finalResponse: ChatResponse | Null = null
    var reading = true
    while reading do
      streamCh.read() match
        case Right(Right(event)) =>
          outCh.send(Right(AgentStreamEvent.Stream(event)))
          event match
            case StreamEvent.Done(response) =>
              finalResponse = response
              reading = false
            case _ =>
        case Right(Left(llmError)) =>
          outCh.send(Left(AgentError(llmError.description)))
          outCh.close()
          throw RuntimeException(s"LLM error: ${llmError.description}")
        case Left(_) => // channel closed
          reading = false
    if finalResponse == null then
      outCh.send(Left(AgentError("Stream ended without Done event")))
      outCh.close()
      throw RuntimeException("Stream ended without Done event")
    finalResponse

  private def dispatchTool(toolUse: Content.ToolUse): Result[Message, AgentError] =
    tools.find(_.name == toolUse.name) match
      case None =>
        Left(AgentError(s"Unknown tool: ${toolUse.name}"))

      case Some(tool) =>
        tool.parseArgs(toolUse.input) match
          case Left(err) =>
            Left(AgentError(s"Failed to parse args for ${toolUse.name}: ${err.message}"))

          case Right(args) =>
            val result = tool.handle(args.asInstanceOf[tool.ArgType], state)
            Right(Message.toolResult(toolUse.id, result))
