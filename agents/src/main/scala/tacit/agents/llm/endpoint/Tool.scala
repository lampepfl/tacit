package tacit.agents
package llm.endpoint

case class Tool(
  name: String,
  description: String,
  parameters: Tool.Parameters,
)

object Tool:
  case class Parameters(
    properties: Map[String, Property],
    required: List[String] = List.empty,
  )

  case class Property(
    `type`: String,
    description: String = "",
    enumValues: List[String] = List.empty,
  )
