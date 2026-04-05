package tacit.agents
package llm
package agentic

import endpoint.{Endpoint, LLMConfig, LLMError, Message, Content, ChatResponse, FinishReason, ToolSchema}
import tacit.agents.utils.Result
import tacit.agents.utils.Result.ok
import llm.utils.{IsToolArg, ToolArgParsingError}
import scala.util.boundary
import scala.annotation.tailrec

class AgentError(val description: String):
  override def toString: String = s"AgentError: $description"

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
