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
      "commandPermissions" -> io.circe.Json.arr(io.circe.Json.fromString("cat *"))
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

  // ── eval (runtime code synthesis) inside Classified.map ─────────────
  //
  // `Classified.map` requires a pure function `T ->{any.rd} B`. When the
  // body of that lambda is `eval[B]("...")`, the outer compile only sees
  // `eval[B]: B` and cannot peek inside the string. The inner compile is
  // triggered when the eval call runs: it splices the body back into the
  // surrounding source and re-typechecks under cc + safe mode. A body
  // that captures iocap or another capability is rejected then —
  // surfacing as a `RuntimeException("eval failed to compile: ...")`
  // thrown from the eval call.
  //
  // That exception is *swallowed* by `Classified.map`'s `Try` wrapper
  // (the result becomes a `Classified[Failure]`), so we cannot observe
  // the rejection from stdout directly. Instead we route the failed
  // `Classified` through the `secureOutput` sink: `unwrapForSecure`
  // unwraps a `Failure` to the literal `"<classified error: <msg>>"`,
  // putting the original `RuntimeException` message — including the
  // inner cc diagnostic — into the secure file. A successful (i.e.
  // unrejected) body would instead leak the *raw* secret to that sink,
  // so the secure file is what tells us whether the inner compile
  // actually rejected the body.

  /** Run `code` under a context whose secure output mirrors classified
   *  prints to a temp file, then return both streams together with the
   *  `ExecutionResult`. The temp file is deleted before returning. */
  private def runWithSecureSink(code: String): (tacit.executor.ExecutionResult, String) =
    val secureFile = Files.createTempFile("eval-secure-", ".log").nn
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "secureOutput" -> io.circe.Json.fromString(secureFile.toString)
    ))
    given Context = Context(cfg, None)
    try
      val result = ScalaExecutor.execute(code)
      val secureContent = Files.readString(secureFile).nn
      (result, secureContent)
    finally Files.deleteIfExists(secureFile)

  test("eval pure body inside Classified.map: transform actually runs"):
    // A pure body passes cc through the eval inner compile. Beyond just
    // "no error", we verify the transform actually runs by routing the
    // result through the secureOutput sink, where `unwrapForSecure`
    // exposes the unwrapped (transformed) value. Main stdout still
    // sees only the masked form, never the original or transformed
    // secret content. This also doubles as the positive control for
    // the secureOutput observation pattern that the negative tests
    // below rely on.
    val (result, secureContent) = runWithSecureSink("""
      val secret = classify("payload-marker-OK")
      val mapped = secret.map(x => eval[String]("x.reverse + \"-transformed\""))
      println(mapped)
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("Classified(***)"),
      s"main stream should show masked form: ${result.output}")
    assert(!result.output.contains("payload-marker-OK"),
      s"main stream leaked original secret: ${result.output}")
    assert(!result.output.contains("KO-rekram-daolyap"),
      s"main stream leaked transformed secret: ${result.output}")
    // Behaviorally verify the transform: reverse + suffix
    assert(secureContent.contains("KO-rekram-daolyap-transformed"),
      s"secure sink should have the reversed-and-suffixed value: $secureContent")
    assert(!secureContent.contains("classified error"),
      s"successful body should not produce a Failure marker: $secureContent")

  test("agent inside Classified.map compiles (@evalLike body accepted by outer cc)"):
    // `agent[Int]` is `@evalLike`, so the outer compile treats it like
    // `eval` — a pure call — and accepts it inside `Classified.map`'s
    // `T ->{any.rd} B` lambda. No LLM is configured here, so the call
    // throws at runtime, but `Classified.map`'s `Try` wrapper turns that
    // into a `Classified[Failure]`; the REPL run still succeeds and the
    // print shows only the masked form.
    val result = ScalaExecutor.execute("""
      val ss = classify("secret")
      println(ss.map(s => agent[Int]("length of s")))
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("Classified(***)"), s"unexpected output: ${result.output}")

  test("agent inside Classified.map of a readClassified result compiles"):
    // Same shape as above, but the classified value comes from the file
    // system: inside `requestFileSystem`, `readClassified` (on a path
    // matched by `classifiedPaths`) yields a `Classified[String]` whose
    // `.map` lambda calls `agent`. The `@evalLike` agent call is still
    // accepted by the outer cc — that's all this test checks; it doesn't
    // exercise the agent call's behavior. With no LLM the run yields a
    // `Classified[Failure]` and the print stays masked.
    val tmp = Files.createTempDirectory("agent-fs-").nn
    val secretFile = tmp.resolve("secret.txt")
    Files.writeString(secretFile, "hunter2-marker")
    val cfg = Config(libraryConfig = io.circe.Json.obj(
      "classifiedPaths" -> io.circe.Json.arr(io.circe.Json.fromString("secret.txt"))
    ))
    given Context = Context(cfg, None)
    val result = ScalaExecutor.execute(s"""
      val root = "${tmp}"
      val path = "${secretFile}"
      requestFileSystem(root) {
        val c = readClassified(path)
        println(c.map(s => agent[Int]("length of s")))
      }
    """)
    try
      assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
      assert(result.output.contains("Classified(***)"), s"unexpected output: ${result.output}")
      assert(!result.output.contains("hunter2-marker"), s"secret leaked: ${result.output}")
    finally
      Files.deleteIfExists(secretFile)
      Files.deleteIfExists(tmp)

  test("eval body using println (captures iocap) inside Classified.map is rejected"):
    // The body would call api.println (the iocap-capturing one shadowed
    // into scope by the preamble), so the inner compile must reject the
    // splice. We verify three things:
    //   1. main stdout never sees the raw secret
    //   2. the secure sink reports a `<classified error: eval failed
    //      to compile: ...>` rather than the secret
    //   3. that error mentions a capture-set complaint (the cc
    //      diagnostic surfaces verbatim through the eval exception)
    val (result, secureContent) = runWithSecureSink("""
      val secret = classify("LEAK-MARKER-IOCAP-001")
      val mapped = secret.map(x => eval[String]("println(x); x"))
      println(mapped)
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(!result.output.contains("LEAK-MARKER-IOCAP-001"),
      s"secret leaked to main stdout — eval body actually ran! ${result.output}")
    assert(!secureContent.contains("LEAK-MARKER-IOCAP-001"),
      s"secret leaked to secure sink — eval body actually ran! $secureContent")
    assert(secureContent.contains("classified error"),
      s"expected Classified to wrap a Failure, secure sink: $secureContent")
    assert(secureContent.contains("eval failed to compile"),
      s"expected eval inner-compile failure surfaced via secure sink: $secureContent")
    val lc = secureContent.toLowerCase
    assert(lc.contains("iocap") || lc.contains("capture"),
      s"expected cc diagnostic about iocap/capture in secure sink: $secureContent")

  test("eval body writing file via FileSystem inside Classified.map is rejected at runtime"):
    // The outer compile only sees `eval[String]: String` — it cannot
    // peek inside the body string, so `secret.map(s => eval[String](...))`
    // type-checks even though the body would capture the `FileSystem`
    // capability that `requestFileSystem` introduced. The violation
    // surfaces when the eval inner-compile runs: splicing the body back
    // into the enclosing source re-typechecks it under cc + safe mode,
    // `Classified.map`'s `any.rd` constraint rejects the captured
    // capability, and `eval` throws `RuntimeException("eval failed to
    // compile: ...")`. `Classified.map`'s `Try` swallows that into a
    // `Classified[Failure]`, observed via the secure sink. The body
    // never runs, so the file is never written.
    val sentinel = Files.createTempDirectory("eval-fs-leak-").nn
    val target = sentinel.resolve("leaked.txt")
    val (result, secureContent) = runWithSecureSink(s"""
      val sentinelPath = "${sentinel}"
      val targetPath = "${target}"
      val secret = classify("LEAK-MARKER-FS-002")
      val mapped = requestFileSystem(sentinelPath) {
        secret.map(s => eval[String]("access(targetPath).write(s); s"))
      }
      println(mapped)
    """)
    try
      assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
      assert(!Files.exists(target), s"eval body executed and wrote $target")
      assert(!result.output.contains("LEAK-MARKER-FS-002"),
        s"secret leaked to main stdout: ${result.output}")
      assert(!secureContent.contains("LEAK-MARKER-FS-002"),
        s"secret leaked to secure sink: $secureContent")
      assert(secureContent.contains("eval failed to compile"),
        s"expected eval inner-compile failure surfaced via secure sink: $secureContent")
      val lc = secureContent.toLowerCase
      assert(lc.contains("capture") || lc.contains("contextual"),
        s"expected cc diagnostic about capture/contextual in secure sink: $secureContent")
    finally
      Files.deleteIfExists(target)
      Files.deleteIfExists(sentinel)

  test("eval body calling requestFileSystem inside Classified.map is rejected"):
    // `requestFileSystem` itself requires `iocap`, so capturing it from
    // inside `Classified.map`'s pure lambda also violates cc — same
    // shape as the println case but routed through the FS-request entry
    // point. The eval body never runs; the file is never created.
    val sentinel = Files.createTempDirectory("eval-iocap-leak-").nn
    val target = sentinel.resolve("leaked.txt")
    val (result, secureContent) = runWithSecureSink(s"""
      val sentinelPath = "${sentinel}"
      val targetPath = "${target}"
      val secret = classify("LEAK-MARKER-IOCAP-003")
      val mapped = secret.map { content =>
        eval[String]("requestFileSystem(sentinelPath) { access(targetPath).write(content); content }")
      }
      println(mapped)
    """)
    try
      assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
      assert(!Files.exists(target), s"eval body executed and wrote $target")
      assert(!result.output.contains("LEAK-MARKER-IOCAP-003"),
        s"secret leaked: ${result.output}")
      assert(!secureContent.contains("LEAK-MARKER-IOCAP-003"),
        s"secret leaked to secure sink: $secureContent")
      assert(secureContent.contains("eval failed to compile"),
        s"expected inner-compile failure in secure sink: $secureContent")
      val lc = secureContent.toLowerCase
      assert(lc.contains("iocap") || lc.contains("capture"),
        s"expected cc diagnostic about iocap/capture: $secureContent")
    finally
      Files.deleteIfExists(target)
      Files.deleteIfExists(sentinel)

  // ── eval composed with Interface functions (no Classified.map) ──────
  //
  // Outside the `any.rd` straitjacket of `Classified.map`, eval is just
  // a runtime "compile this string in the surrounding lexical context"
  // primitive. These tests exercise it together with the rest of the
  // capability API, to confirm the inner compile picks up REPL bindings,
  // contextual capabilities, and type pinning correctly — and that
  // values cross the eval/REPL classloader boundary safely. All these
  // tests run with the default Context (safe mode on).

  test("eval at top level computes a typed value"):
    val result = ScalaExecutor.execute("""
      val n = 7
      eval[Int]("n * n + 1")
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("50"), s"expected 50 in output: ${result.output}")

  test("eval body resolves REPL-defined def by name"):
    // Identifiers introduced by a previous statement (or earlier in the
    // same block) resolve inside the body via the bindings array filled
    // by the rewriter.
    val result = ScalaExecutor.execute("""
      def square(j: Int) = j * j
      val xs = List(2, 3, 4)
      xs.map(x => eval[Int]("square(x)"))
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("4") && result.output.contains("9") && result.output.contains("16"),
      s"expected List(4, 9, 16): ${result.output}")

  test("eval body inside requestFileSystem can use FileSystem capability"):
    // Inside `requestFileSystem`, a `FileSystem` capability is in scope
    // (a contextual function parameter). The eval body inherits that
    // scope through `enclosingSource`, so `access(...).write(...)`
    // resolves directly inside the body. Path and payload are bound
    // to REPL `val`s so the body has no nested string-escape gymnastics.
    val tmp = Files.createTempDirectory("eval-fs-ok-").nn
    val file = tmp.resolve("hello.txt")
    val result = ScalaExecutor.execute(s"""
      val root = "${tmp}"
      val target = "${file}"
      val payload = "eval wrote me"
      requestFileSystem(root) {
        eval[String]("access(target).write(payload); access(target).read()")
      }
    """)
    try
      assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
      assert(result.output.contains("eval wrote me"),
        s"expected file content in output: ${result.output}")
      assert(Files.exists(file), s"file should have been written")
      assertEquals(Files.readString(file).nn, "eval wrote me")
    finally
      Files.deleteIfExists(file)
      Files.deleteIfExists(tmp)

  test("eval body inside requestExecPermission can call exec"):
    // The eval body directly calls `exec`, picking up the
    // `ProcessPermission` from the surrounding scope.
    val result = ScalaExecutor.execute("""
      val cmd = "echo"
      val arg = "eval-and-exec"
      requestExecPermission(Set(cmd)) {
        eval[String]("exec(cmd, List(arg)).stdout.trim")
      }
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("eval-and-exec"),
      s"expected echo output: ${result.output}")

  test("eval body composing grepRecursive through FileSystem capability"):
    // Body calls grepRecursive directly and projects across user
    // library types (`GrepMatch`) — exercises both contextual
    // capability access and cross-classloader value handling.
    val tmp = Files.createTempDirectory("eval-grep-").nn
    val a = tmp.resolve("a.scala")
    val b = tmp.resolve("b.scala")
    val c = tmp.resolve("c.txt")
    Files.writeString(a, "val needle = 1\n")
    Files.writeString(b, "val something = 2\n")     // no match
    Files.writeString(c, "val needle = 3\n")        // .txt — wrong glob
    val result = ScalaExecutor.execute(s"""
      val root = "${tmp}"
      val pattern = "needle"
      val glob = "*.scala"
      requestFileSystem(root) {
        eval[List[String]]("grepRecursive(root, pattern, glob).map(_.file).distinct.sorted")
      }
    """)
    try
      assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
      // Behavior: only a.scala matches both the pattern and the glob.
      assert(result.output.contains("a.scala"),
        s"a.scala (matching file) missing from output: ${result.output}")
      assert(!result.output.contains("b.scala"),
        s"b.scala (no match) should not appear: ${result.output}")
      assert(!result.output.contains("c.txt"),
        s"c.txt (wrong glob) should not appear: ${result.output}")
    finally
      Files.deleteIfExists(a); Files.deleteIfExists(b); Files.deleteIfExists(c)
      Files.deleteIfExists(tmp)

  test("eval body type mismatch surfaces as runtime exception"):
    // The expected type is pinned by `eval[Int]`, but the body returns
    // a String. The inner compile rejects this at runtime — and unlike
    // the Classified.map cases, there's no `Try` to swallow it, so the
    // RuntimeException reaches the REPL and shows up in `output`.
    // Bind the body string to a val to avoid escape gymnastics.
    val result = ScalaExecutor.execute("""
      val body = "\"not an int\""
      eval[Int](body)
    """)
    val combined = (result.output + result.error.getOrElse("")).toLowerCase
    assert(combined.contains("eval failed to compile") || combined.contains("evalcompile"),
      s"expected eval inner-compile failure, got: ${result.output} / err=${result.error}")
    assert(combined.contains("found") && combined.contains("required"),
      s"expected Found/Required diagnostic: ${result.output}")

  test("evalSafe returns a Failure for an ill-typed body"):
    // Non-throwing flavor: the diagnostic is carried as data instead.
    val result = ScalaExecutor.execute("""
      val body = "\"still a string\""
      val r = Eval.evalSafe[Int](body)
      r.isSuccess
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    // Behavior: evalSafe should report the body did not type-check.
    assert(result.output.contains("false"),
      s"expected EvalResult.isSuccess == false: ${result.output}")
