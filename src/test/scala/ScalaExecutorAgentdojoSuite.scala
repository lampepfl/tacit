package tacit.executor

import tacit.core.{AgentdojoDomain, Config, Context}

class ScalaExecutorAgentdojoSuite extends munit.FunSuite:
  test("slack agentdojo preamble is available"):
    given Context = Context(
      Config(
        agentdojoPort = Some(50718),
        agentdojoDomain = Some(AgentdojoDomain.Slack),
        agentdojoSecureChannel = Some("/tmp/tacit-slack-secure.txt")
      ),
      None
    )

    val preamble = ScalaExecutor.libraryPreamble
    assert(preamble.contains("import tacit.library.slack.*"))
    assert(preamble.contains("""val slack: SlackService = new SlackImpl("http://localhost:50718/mcp", "/tmp/tacit-slack-secure.txt")"""))
    assert(preamble.contains("import slack.*"))
