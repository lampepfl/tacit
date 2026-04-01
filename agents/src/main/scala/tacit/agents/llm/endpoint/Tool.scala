package tacit.agents
package llm.endpoint

case class ToolSchema(
  name: String,
  description: String,
  parameters: ToolSchema.Parameters,
)

object ToolSchema:
  case class Parameters(
    properties: Map[String, Property],
    required: List[String] = List.empty,
  )

  case class Property(
    `type`: String,
    description: String = "",
    enumValues: List[String] = List.empty,
    items: Option[Property] = None,
  )
