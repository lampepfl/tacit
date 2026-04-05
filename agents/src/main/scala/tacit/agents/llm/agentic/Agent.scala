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

class Agent[S <: AgentState](initialState: S):
  var state: S = initialState
  var tools: List[AgentTool[S]] = Nil

  def withTool(tool: AgentTool[S]): this.type =
    tools = tools :+ tool
    this

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
