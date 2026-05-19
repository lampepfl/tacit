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
    assert(preamble.contains("""val workspace: WorkspaceService = new WorkspaceImpl("http://127.0.0.1:50718/mcp", "/tmp/tacit-workspace-secure.txt", "openrouter", "anthropic/claude-sonnet-4-6")"""))
    assert(preamble.contains("import workspace.*"))
