package capybaraclaw

import tacit.core.{Context, Config}
import tacit.executor.ReplSession
import tacit.agents.llm.endpoint.*
import tacit.agents.llm.agentic.{Agent, AgentState, AgentError}
import tacit.agents.llm.utils.IsToolArg
import tacit.agents.utils.Result

case class EvalScalaArgs(code: String) derives IsToolArg

/** Agent class for Claw. */
class ClawAgent(val workDir: String):
  val clawConfig: ClawConfig = ClawConfig.load(workDir)
  val agentConfig: AgentConfig = AgentConfig.fromClawConfig(clawConfig, workDir)

  private given Context = Context(
    Config(
      restrictedWorkingDir = Some(workDir),
      classifiedPaths = clawConfig.classifiedPaths.map(p => java.io.File(workDir, p).getCanonicalPath).toSet,
    ),
    recorder = None,
  )

  private given Endpoint = clawConfig.provider match
    case "anthropic"  => AnthropicEndpoint.createFromEnv()
    case "openai"     => OpenAIEndpoint.createFromEnv()
    case "openrouter" => OpenRouterEndpoint.createFromEnv()
    case other        => throw RuntimeException(s"Unknown provider: $other")

  private val repl: ReplSession = ReplSession.create

  private val agent: Agent =
    val a = new Agent:
      type State = AgentState
      def getInitState = new AgentState:
        val llmConfig = agentConfig.toLLMConfig

    a.handle[EvalScalaArgs]("evaluate_scala", "Evaluate a Scala expression in a persistent REPL session"): (args, _) =>
      val result = repl.execute(args.code)
      if result.success then
        if result.output.nonEmpty then result.output
        else "(executed successfully, no output)"
      else
        val msg = StringBuilder("Execution failed.\n")
        if result.output.nonEmpty then msg.append(s"Output:\n${result.output}\n")
        result.error.foreach(e => msg.append(s"Error:\n$e\n"))
        msg.toString

    a

  def ask(
    message: String,
    onToolCall: Option[(String, String, String) => Unit] = None,
  ): Result[ChatResponse, AgentError] =
    agent.ask(message, onToolCall)

  def printStartupInfo(): Unit =
    val clawJsonExists = java.io.File(workDir, "claw.json").exists()
    val clawMdExists = java.io.File(workDir, "CLAW.md").exists()
    println("Capybara Claw")
    println(s"  workdir  : $workDir")
    println(s"  provider : ${clawConfig.provider}")
    println(s"  model    : ${clawConfig.model}")
    println(s"  thinking : ${agentConfig.thinking.getOrElse("off")}")
    println(s"  claw.json: ${if clawJsonExists then "found" else "defaults"}")
    println(s"  CLAW.md  : ${if clawMdExists then "found" else "not found"}")
    if clawConfig.classifiedPaths.nonEmpty then
      println(s"  classify : ${clawConfig.classifiedPaths.mkString(", ")}")
    println()
