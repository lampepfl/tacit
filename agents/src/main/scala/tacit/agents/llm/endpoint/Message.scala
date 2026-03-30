package tacit.agents
package llm.endpoint

enum Role:
  case System, User, Assistant

enum Content:
  case Text(text: String)
  case ToolUse(id: String, name: String, input: String)
  case ToolResult(toolUseId: String, content: String, isError: Boolean = false)

case class Message(role: Role, content: List[Content]):
  def text: String =
    val texts = content.collect:
      case Content.Text(t) => t
    texts.mkString

object Message:
  def user(text: String): Message =
    Message(Role.User, List(Content.Text(text)))

  def assistant(text: String): Message =
    Message(Role.Assistant, List(Content.Text(text)))

  def system(text: String): Message =
    Message(Role.System, List(Content.Text(text)))

  def toolResult(toolUseId: String, content: String, isError: Boolean = false): Message =
    Message(Role.User, List(Content.ToolResult(toolUseId, content, isError)))

case class ChatResponse(
  message: Message,
  finishReason: FinishReason,
  usage: Option[Usage] = None,
)

enum FinishReason:
  case Stop, MaxTokens, ToolUse
  case Other(value: String)

case class Usage(
  inputTokens: Long,
  outputTokens: Long,
)

enum StreamEvent:
  case Delta(text: String)
  case ToolCallStart(index: Int, id: String, name: String)
  case ToolCallDelta(index: Int, argumentDelta: String)
  case Done(response: ChatResponse)
