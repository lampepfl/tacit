package tacit.agents
package llm
package agentic

import utils.{IsToolArg, ToolArgParsingError}
import endpoint.ToolSchema
import tacit.agents.utils.Result

trait ToolType:
  def name: String
  def description: String

  type Args: IsToolArg

  def toolSchema: ToolSchema =
    ToolSchema(name, description, parameters = summon[IsToolArg[Args]].schema)

  def parseArgs(input: String): Result[Args, ToolArgParsingError] =
    summon[IsToolArg[Args]].parse(input)
