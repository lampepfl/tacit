package tacit.executor

import tacit.core.{AgentdojoDomain, Config, Context}

class ScalaExecutorAgentdojoSuite extends munit.FunSuite:
  test("workspace agentdojo preamble is available"):
    given Context = Context(
      Config(
        agentdojoPort = Some(50718),
        agentdojoDomain = Some(AgentdojoDomain.Workspace),
        agentdojoSecureChannel = Some("/tmp/tacit-workspace-secure.txt")
      ).withLlm("provider", "openrouter")
       .withLlm("model", "anthropic/claude-sonnet-4-6"),
      None
    )

    val preamble = ManagedRepl.libraryPreamble
    assert(preamble.contains("import tacit.library.workspace.*"))
    assert(preamble.contains("""val service: WorkspaceService = new WorkspaceImpl("http://127.0.0.1:50718/mcp", "/tmp/tacit-workspace-secure.txt", "openrouter", "anthropic/claude-sonnet-4-6")"""))
    assert(preamble.contains("import service.*"))

  test("slack agentdojo preamble is available"):
    given Context = Context(
      Config(
        agentdojoPort = Some(50212),
        agentdojoDomain = Some(AgentdojoDomain.Slack),
        agentdojoSecureChannel = Some("/tmp/tacit-slack-secure.txt")
      ).withLlm("provider", "openrouter")
       .withLlm("model", "anthropic/claude-sonnet-4-6"),
      None
    )

    val preamble = ManagedRepl.libraryPreamble
    assert(preamble.contains("import tacit.library.slack.*"))
    assert(preamble.contains("""val service: SlackService = new SlackImpl("http://127.0.0.1:50212/mcp", "/tmp/tacit-slack-secure.txt", "openrouter", "anthropic/claude-sonnet-4-6")"""))
    assert(preamble.contains("import service.*"))

  test("banking agentdojo preamble is available"):
    given Context = Context(
      Config(
        agentdojoPort = Some(50771),
        agentdojoDomain = Some(AgentdojoDomain.Banking),
        agentdojoSecureChannel = Some("/tmp/tacit-banking-secure.txt")
      ).withLlm("provider", "openrouter")
       .withLlm("model", "anthropic/claude-sonnet-4-6"),
      None
    )

    val preamble = ManagedRepl.libraryPreamble
    assert(preamble.contains("import tacit.library.banking.*"))
    assert(preamble.contains("""val service: BankingService = new BankingImpl("http://127.0.0.1:50771/mcp", "/tmp/tacit-banking-secure.txt", "openrouter", "anthropic/claude-sonnet-4-6")"""))
    assert(preamble.contains("import service.*"))

  test("travel agentdojo preamble is available"):
    given Context = Context(
      Config(
        agentdojoPort = Some(50969),
        agentdojoDomain = Some(AgentdojoDomain.Travel),
        agentdojoSecureChannel = Some("/tmp/tacit-travel-secure.txt")
      ).withLlm("provider", "openrouter")
       .withLlm("model", "anthropic/claude-sonnet-4-6"),
      None
    )

    val preamble = ManagedRepl.libraryPreamble
    assert(preamble.contains("import tacit.library.travel.*"))
    assert(preamble.contains("""val service: TravelService = new TravelImpl("http://127.0.0.1:50969/mcp", "/tmp/tacit-travel-secure.txt", "openrouter", "anthropic/claude-sonnet-4-6")"""))
    assert(preamble.contains("import service.*"))
