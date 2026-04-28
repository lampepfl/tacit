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
    assert(r1.success, s"session execution failed: ${r1.error}")
    assert(r1.output.contains("true"), s"unexpected output: ${r1.output}")

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
    assert(r2.success)
    assert(r2.output.contains("true"), s"unexpected output: ${r2.output}")

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

  test("commandPermissions allows matching command via REPL"):
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("echo"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        exec("echo", List("hi")).stdout.trim
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("hi"), s"unexpected output: ${result.output}")

  test("commandPermissions blocks unmatched command via REPL"):
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("echo"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("ls")) {
        exec("ls", List.empty)
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.toLowerCase.contains("permitted pattern"),
      s"expected permissions error, got: ${result.output}")

  test("commandPermissions overrides strict mode for file ops via REPL"):
    // With strict mode alone, `cat` would be blocked. commandPermissions
    // overrides that and allows it.
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "strictMode" -> io.circe.Json.fromBoolean(true),
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("cat"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("cat")) {
        // /dev/null exists on macOS and Linux; cat of it is empty output.
        exec("cat", List("/dev/null")).exitCode
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(!result.output.toLowerCase.contains("strict mode"),
      s"strict mode should have been overridden, got: ${result.output}")
    assert(result.output.contains("0"), s"expected exit code 0, got: ${result.output}")

  test("commandPermissions with glob pattern via REPL"):
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("ec*"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        exec("echo", List("ok")).stdout.trim
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("ok"), s"unexpected output: ${result.output}")

  test("commandPermissions rejects scope command outside policy at entry"):
    // Entry-time subset check: requestExecPermission(Set("rm")) must fail
    // immediately because policy only permits "echo" — no exec call needed.
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("echo"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("rm")) {
        "unreachable"
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.toLowerCase.contains("not permitted by server policy"),
      s"expected entry-time permissions error, got: ${result.output}")

  test("commandPermissions arg-aware pattern passes scope entry but filters args"):
    // Policy "sbt run *" allows command-word "sbt" at scope entry,
    // but per-invocation matching still rejects `sbt clean`.
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("sbt run *"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("sbt")) {
        exec("sbt", List("clean"))
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.toLowerCase.contains("permitted pattern"),
      s"expected runtime arg-aware error, got: ${result.output}")

  test("networkPermissions blocks unmatched host via REPL"):
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "networkPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("*.example.com"))
    ))
    given Context = Context(cfg, None)
    // requestNetwork allows "localhost" at the scope level, but global policy
    // only permits *.example.com, so validateHost must throw.
    val result = ScalaExecutor.execute("""
      requestNetwork(Set("localhost")) {
        httpGet("http://localhost:1/")
      }
    """)
    assert(result.success, s"should compile but throw at runtime: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("SecurityException") || result.output.toLowerCase.contains("permitted pattern"),
      s"expected permissions error, got: ${result.output}")

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

  // ── Safe mode defense-in-depth tests ──────────────────────────────

  test("safe mode rejects mutating outer var inside Classified.map"):
    // `Classified.map` requires `T -> B` (a pure function). Mutating a `var`
    // from the enclosing scope is a side effect that makes the lambda impure.
    // Only safe mode catches this, because capture tracking treats mutable
    // variables as capabilities. Without safe mode, this compiles silently.
    assertCompileError(
      """var counter = 0
        |val c = classify("secret")
        |val result = c.map { s =>
        |  counter += 1
        |  s.toUpperCase
        |}
        |result""".stripMargin,
      "capture"
    )

  test("safe mode allows reading outer val inside Classified.map"):
    // Reading an immutable `val` does not capture any capability, so it's
    // a pure operation and safe mode allows it inside Classified.map.
    val result = ScalaExecutor.execute("""
      val prefix = "PREFIX:"
      val c = classify("secret")
      c.map(s => prefix + s.toUpperCase).toString
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")

  test("safe mode allows legitimate Classified.map with pure function"):
    val result = ScalaExecutor.execute("""
      val c = classify("secret")
      c.map(_.trim).toString
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")

  test("safe mode rejects println inside Classified.map (side channel)"):
    assertCompileError(
      """val secret = classify("password")
        |secret.map(x => { println(x); x })""".stripMargin,
      "capture"
    )

  test("safe mode allows file system operations with capability"):
    val result = ScalaExecutor.execute("""
      requestFileSystem("/tmp") {
        val f = access("/tmp/safe-mode-test.txt")
        f.write("safe mode works")
        val content = f.read()
        f.delete()
        content
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("safe mode works"), s"unexpected output: ${result.output}")

  test("safe mode allows exec with permitted command"):
    val result = ScalaExecutor.execute("""
      requestExecPermission(Set("echo")) {
        exec("echo", List("safe-mode-test")).stdout.trim
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("safe-mode-test"), s"unexpected output: ${result.output}")

  test("safe mode allows network with permitted host"):
    val result = ScalaExecutor.execute("""
      requestNetwork(Set("localhost")) {
        // Validates host without making a real connection
        "network capability available"
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")

  test("safe mode rejects exec without ProcessPermission capability"):
    assertCompileError(
      """exec("echo", List("hi"))""",
      "no given instance of type tacit.library.ProcessPermission"
    )
