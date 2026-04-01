package tacit.agents
package llm
package computation

import utils.{IsToolArg, ToolArgParsingError}
import endpoint.ToolSchema
import tacit.agents.utils.Result

trait ToolType:
  def name: String
  def description: String

  type Args: IsToolArg

  /** The ToolSchema for this tool, ready to pass to an Endpoint. */
  def toolSchema: ToolSchema =
    ToolSchema(name = name, description = description, parameters = summon[IsToolArg[Args]].schema)

  /** Parse a raw JSON string into the typed Args. */
  def parseArgs(input: String): Result[Args, ToolArgParsingError] =
    summon[IsToolArg[Args]].parse(input)
