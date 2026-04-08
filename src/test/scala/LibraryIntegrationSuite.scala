import tacit.executor.{ScalaExecutor, SessionManager}
import tacit.core.{Context, Config}
import java.nio.file.Files

class LibraryIntegrationSuite extends munit.FunSuite:
  given Context = Context(Config(), None)

  /** Helper: assert that code fails to compile in the REPL with an error matching `pattern`. */
  private def assertCompileError(code: String, pattern: String)(using loc: munit.Location): Unit =
    val result = ScalaExecutor.execute(code)
    assert(!result.success, s"expected compilation failure, got success with: ${result.output}")
    val output = result.output.toLowerCase
    assert(output.contains("error"), s"expected a compile error, got: ${result.output}")
    assert(output.contains(pattern.toLowerCase), s"expected error containing '$pattern', got: ${result.output}")

  // ── Positive: capability API via REPL ─────────────────────────────

  test("requestFileSystem and access a path"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") { access("/tmp").exists }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("true"), s"unexpected output: ${result.output}")

  test("requestFileSystem and read directory children"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") { access("/tmp").isDirectory }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("true"), s"unexpected output: ${result.output}")

  test("requestFileSystem write and read back"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") {
        val f = access("/tmp/safe-exec-mcp-test.txt")
        f.write("hello from test")
        val content = f.read()
        f.delete()
        content
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("hello from test"), s"unexpected output: ${result.output}")

  test("requestExecPermission and exec a command"):
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        exec("echo", List("hello")).stdout.trim
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("hello"), s"unexpected output: ${result.output}")

  test("requestExecPermission and execOutput"):
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        execOutput("echo", List("world"))
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("world"), s"unexpected output: ${result.output}")

  test("grep in filesystem"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") {
        val f = access("/tmp/safe-exec-mcp-grep-test.txt")
        f.write("line one\nfind me here\nline three")
        val matches = grep("/tmp/safe-exec-mcp-grep-test.txt", "find me")
        f.delete()
        matches.map(_.line)
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("find me here"), s"unexpected output: ${result.output}")

  test("library available in session"):
    val manager = new SessionManager
    val sessionId = manager.createSession()

    val r1 = manager.executeInSession(sessionId, """
      requestFileSystem("/tmp") { access("/tmp").exists }
    """)
    assert(r1.isRight, s"session execution failed: $r1")
    assert(r1.toOption.get.output.contains("true"), s"unexpected output: ${r1.toOption.get.output}")

    manager.deleteSession(sessionId)

  test("calling foreach(println) on the result of grepRecursive"):
    val result = ScalaExecutor.execute("""
      requestFileSystem(".") {
        val allEntries = access("./projects/webapp").walk()

        // Collect info as plain strings first, then print outside the lambda
        val lines = allEntries.map(e => s"${if e.isDirectory then "[DIR] " else "[FILE]"} ${e.path}")

        lines.foreach(println)
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(!result.output.contains("Type Mismatch Error"), s"unexpected output: ${result.output}")

  test("filter out all non-file with walk on root"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/") {
        access("/").walk().filterNot(_.isDirectory).map(_.path)
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(!result.output.contains("Type Mismatch Error"), s"unexpected output: ${result.output}")

  // ── Negative tests: capture checking prevents capability leaks ──

  test("cannot leak FileEntry out of requestFileSystem"):
    assertCompileError(
      """val leaked = requestFileSystem("/tmp") { access("/tmp") }""",
      "leaks into outer capture set"
    )

  test("cannot leak closure capturing FileSystem"):
    assertCompileError(
      """val fn = requestFileSystem("/tmp") { () => access("/tmp").read() }""",
      "leaks into outer capture set"
    )

  test("cannot use access without FileSystem capability"):
    assertCompileError(
      """access("/tmp")""",
      "no given instance of type tacit.library.FileSystem"
    )

  test("cannot use exec without ProcessPermission capability"):
    assertCompileError(
      """exec("echo", List("hi"))""",
      "no given instance of type tacit.library.ProcessPermission"
    )

  test("cannot use httpGet without Network capability"):
    assertCompileError(
      """httpGet("https://example.com")""",
      "no given instance of type tacit.library.Network"
    )

  test("println inside Classified.map is rejected by capture checker"):
    assertCompileError(
      """
      val secret = classify("password")
      secret.map(x => { println(x); x })
      """,
      "capture"
    )

  test("write inside Classified.map is rejected by capture checker"):
    assertCompileError(
      """
      val secret = classify("password")
      requestFileSystem("/tmp") {
        secret.map(x => { access("/tmp/secret.txt").write(x); x })
      }
      """,
      "capture"
    )

  test("requestFileSystem inside Classified.map is rejected by capture checker"):
    assertCompileError(
      """
      val secret = classify("password")
      secret.map { content =>
        requestFileSystem("/tmp") {
          access("/tmp/leaked.txt").write(content)
        }
        content
      }
      """,
      "capture"
    )

  test("session preserves state across library calls"):
    val manager = new SessionManager
    val sessionId = manager.createSession()

    manager.executeInSession(sessionId, """
      val testResult = requestFileSystem("/tmp") { access("/tmp").isDirectory }
    """)

    val r2 = manager.executeInSession(sessionId, "testResult")
    assert(r2.isRight)
    assert(r2.toOption.get.output.contains("true"), s"unexpected output: ${r2.toOption.get.output}")

    manager.deleteSession(sessionId)

  // ── Classified path bypass tests ──

  test("requestFileSystem with subdirectory of classified path blocks read"):
    // Setup: create a temp dir with secrets/docs/secret.txt
    val tmpDir = Files.createTempDirectory("classified-bypass-test")
    val secretsDir = tmpDir.resolve("secrets")
    val docsDir = secretsDir.resolve("docs")
    Files.createDirectories(docsDir)
    val secretFile = docsDir.resolve("secret.txt")
    Files.writeString(secretFile, "TOP SECRET DATA")

    // Configure classified paths to include secrets/
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "classifiedPaths" -> io.circe.Json.arr(io.circe.Json.fromString(secretsDir.toString))
    ))
    given Context = Context(cfg, None)

    // Attempt the bypass: requestFileSystem on secrets/docs (a subdirectory of classified)
    val result = ScalaExecutor.execute(s"""
      requestFileSystem("${docsDir}") {
        access("${secretFile}").read()
      }
    """)

    // Cleanup
    Files.deleteIfExists(secretFile)
    Files.deleteIfExists(docsDir)
    Files.deleteIfExists(secretsDir)
    Files.deleteIfExists(tmpDir)

    // The read must be blocked: output should contain SecurityException, not the secret
    assert(!result.output.contains("TOP SECRET DATA"),
      s"classified data leaked! output: ${result.output}")
    assert(result.output.toLowerCase.contains("access denied") || result.output.toLowerCase.contains("classified"),
      s"expected security error about classified path, got: ${result.output}")

  test("classified pattern with glob wildcard blocks matching paths"):
    val tmpDir = Files.createTempDirectory("classified-glob-test")
    val configDir = tmpDir.resolve("config")
    val prodDir = configDir.resolve("prod")
    val keysDir = prodDir.resolve("keys")
    Files.createDirectories(keysDir)
    val keyFile = keysDir.resolve("secret.pem")
    Files.writeString(keyFile, "PRIVATE KEY DATA")

    // Pattern: config/*/keys — should match config/prod/keys and descendants
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "classifiedPaths" -> io.circe.Json.arr(
        io.circe.Json.fromString(s"${configDir}/*/keys")
      )
    ))
    given Context = Context(cfg, None)

    val result = ScalaExecutor.execute(s"""
      requestFileSystem("${tmpDir}") {
        access("${keyFile}").read()
      }
    """)

    Files.deleteIfExists(keyFile)
    Files.deleteIfExists(keysDir)
    Files.deleteIfExists(prodDir)
    Files.deleteIfExists(configDir)
    Files.deleteIfExists(tmpDir)

    assert(!result.output.contains("PRIVATE KEY DATA"),
      s"classified data leaked! output: ${result.output}")
    assert(result.output.toLowerCase.contains("access denied") || result.output.toLowerCase.contains("classified"),
      s"expected security error, got: ${result.output}")

  test("classified component pattern blocks matching paths via REPL"):
    val tmpDir = Files.createTempDirectory("classified-component-test")
    val sshDir = tmpDir.resolve(".ssh")
    Files.createDirectories(sshDir)
    val keyFile = sshDir.resolve("id_rsa")
    Files.writeString(keyFile, "SSH PRIVATE KEY")

    // Component pattern: .ssh (no slash) should match .ssh at any depth
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "classifiedPaths" -> io.circe.Json.arr(io.circe.Json.fromString(".ssh"))
    ))
    given Context = Context(cfg, None)

    val result = ScalaExecutor.execute(s"""
      requestFileSystem("${tmpDir}") {
        access("${keyFile}").read()
      }
    """)

    Files.deleteIfExists(keyFile)
    Files.deleteIfExists(sshDir)
    Files.deleteIfExists(tmpDir)

    assert(!result.output.contains("SSH PRIVATE KEY"),
      s"classified data leaked! output: ${result.output}")

  // ── Runtime security: capability enforcement via REPL ────────────

  test("exec rejects disallowed command via REPL"):
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        exec("rm", List("-rf", "/"))
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.contains("Access denied"),
      s"expected security error, got: ${result.output}")

  test("strict mode blocks file commands via exec in REPL"):
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "strictMode" -> io.circe.Json.fromBoolean(true)
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("cat")) {
        exec("cat", List("/etc/passwd"))
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.toLowerCase.contains("strict") || result.output.contains("SecurityException"),
      s"expected strict mode error, got: ${result.output}")

  test("requestFileSystem blocks path escape via REPL"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") {
        access("/etc/passwd").read()
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.contains("Access denied"),
      s"expected security error, got: ${result.output}")

  test("file append and delete via REPL"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") {
        val f = access("/tmp/safe-exec-mcp-append-test.txt")
        f.write("line1\n")
        f.append("line2\n")
        val content = f.read()
        f.delete()
        (content, f.exists)
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("line1"), s"unexpected output: ${result.output}")
    assert(result.output.contains("line2"), s"unexpected output: ${result.output}")
    assert(result.output.contains("false"), s"file should not exist after delete: ${result.output}")

  // ── Compile-time: additional capability leak prevention ──────────

  test("cannot leak ProcessPermission out of requestExecPermission"):
    assertCompileError(
      """val leaked = requestExecPermission(Set("echo")) { summon[ProcessPermission] }""",
      "leaks into outer capture set"
    )

  test("cannot leak Network out of requestNetwork"):
    assertCompileError(
      """val leaked = requestNetwork(Set("example.com")) { summon[Network] }""",
      "leaks into outer capture set"
    )

  // ── Timeout behavior ────────────────────────────────────────────

  test("exec timeout in REPL"):
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("sleep")) {
        exec("sleep", List("60"), timeoutMs = 100)
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("timed out") || result.output.contains("RuntimeException"),
      s"expected timeout error, got: ${result.output}")
