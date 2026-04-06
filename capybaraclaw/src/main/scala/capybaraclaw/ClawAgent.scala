package capybaraclaw

import tacit.executor.ReplSession
import tacit.agents.llm.agentic.{Agent, AgentState}
import tacit.agents.llm.utils.IsToolArg

case class EvalScalaArgs(code: String) derives IsToolArg

object ClawAgent:
  def create(config: AgentConfig, repl: ReplSession): Agent =
    val llmConfig = config.toLLMConfig

    val agent = new Agent:
      type State = AgentState
      def getInitState = new AgentState:
        val llmConfig = config.toLLMConfig

    agent.handle[EvalScalaArgs]("evaluate_scala", "Evaluate a Scala expression in a persistent REPL session"): (args, _) =>
      val result = repl.execute(args.code)
      if result.success then
        if result.output.nonEmpty then result.output
        else "(executed successfully, no output)"
      else
        val msg = StringBuilder("Execution failed.\n")
        if result.output.nonEmpty then msg.append(s"Output:\n${result.output}\n")
        result.error.foreach(e => msg.append(s"Error:\n$e\n"))
        msg.toString

    agent
