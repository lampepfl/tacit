package tacit.agents
package llm
package agentic

import utils.{IsToolArg, ToolArgParsingError}
import endpoint.ToolSchema
import tacit.agents.utils.Result

trait AgentTool[-StateType <: AgentState]:
  type ArgType: IsToolArg

  def name: String
  def description: String

  def handle(arg: ArgType, state: StateType): String

  def toolSchema: ToolSchema =
    ToolSchema(name, description, parameters = summon[IsToolArg[ArgType]].schema)

  def parseArgs(input: String): Result[ArgType, ToolArgParsingError] =
    summon[IsToolArg[ArgType]].parse(input)
