package tacit.library

import language.experimental.captureChecking

import caps.unsafe.unsafeAssumePure

import java.nio.file.{Files, Path}
import scala.compiletime.uninitialized
import scala.jdk.CollectionConverters.*

class LibrarySuite extends munit.FunSuite:

  var tmpDir: Path = uninitialized

  override def beforeEach(context: BeforeEach): Unit =
    tmpDir = Files.createTempDirectory("sandbox-test")

  // allowedRoots "/" opts out of the default working-directory bound; these
  // tests operate on per-test temp dirs, not on the bound itself (which has its
  // own dedicated tests below).
  private val interface: Interface^{} = new InterfaceImpl("""{"allowedRoots": ["/"]}""") {
    override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
      new RealFileSystem(root, filter, classifiedPatterns)
  }.unsafeAssumePure

  import interface.*

  given (IOCapability^{}) = null.asInstanceOf[IOCapability]

  override def afterEach(context: AfterEach): Unit =
    if Files.exists(tmpDir) then
      Files.walk(tmpDir).iterator().asScala.toList
        .sortBy(_.toString)(using Ordering[String].reverse)
        .foreach(p => Files.deleteIfExists(p))

  test("read/write file round-trip within allowed root") {
    val filePath = tmpDir.resolve("hello.txt").toString
    requestFileSystem(tmpDir.toString) {
      val file = access(filePath)
      file.write("Hello, sandbox!")
      assertEquals(file.read(), "Hello, sandbox!")
    }
  }

  test("append to file") {
    val filePath = tmpDir.resolve("append.txt").toString
    requestFileSystem(tmpDir.toString) {
      val file = access(filePath)
      file.write("line1\n")
      file.append("line2\n")
      assertEquals(file.read(), "line1\nline2\n")
    }
  }

  test("list directory contents") {
    val aPath = tmpDir.resolve("a.txt").toString
    val bPath = tmpDir.resolve("b.txt").toString
    val dirPath = tmpDir.toString
    requestFileSystem(dirPath) {
      val fs = summon[FileSystem]
      fs.access(aPath).write("a")
      fs.access(bPath).write("b")
      val kids = fs.access(dirPath).children
      assertEquals(kids.length, 2)
      assert(kids.exists(_.name == "a.txt"))
      assert(kids.exists(_.name == "b.txt"))
    }
  }

  test("delete file") {
    val filePath = tmpDir.resolve("doomed.txt").toString
    requestFileSystem(tmpDir.toString) {
      val file = access(filePath)
      file.write("bye")
      assert(file.exists)
      file.delete()
      assert(!file.exists)
    }
  }

  test("reject path outside allowed roots") {
    requestFileSystem(tmpDir.toString) {
      val ex = intercept[SecurityException] {
        access("/etc/passwd")
      }
      assert(ex.getMessage.startsWith("Access denied"))
    }
  }

  test("reject access through a symlink that escapes the root") {
    // A symlink inside the root that points outside it must not be a way out:
    // RealFileSystem resolves symlinks (toRealPath) before the startsWith check.
    // This exercises the real-disk path that VirtualFileSystem cannot model.
    val outside = Files.createTempDirectory("sandbox-outside")
    try
      Files.writeString(outside.resolve("secret.txt"), "OUTSIDE SECRET")
      Files.createSymbolicLink(tmpDir.resolve("escape"), outside)
      requestFileSystem(tmpDir.toString) {
        val ex = intercept[SecurityException] {
          access(tmpDir.resolve("escape/secret.txt").toString).read()
        }
        assert(ex.getMessage.nn.startsWith("Access denied"))
      }
    finally
      Files.deleteIfExists(outside.resolve("secret.txt"))
      Files.deleteIfExists(outside)
  }

  test("readLines returns file as list of lines") {
    val filePath = tmpDir.resolve("lines.txt").toString
    requestFileSystem(tmpDir.toString) {
      val file = access(filePath)
      file.write("alpha\nbeta\ngamma")
      assertEquals(file.readLines(), List("alpha", "beta", "gamma"))
    }
  }

  test("grep finds pattern in file") {
    val filePath = tmpDir.resolve("data.txt").toString
    requestFileSystem(tmpDir.toString) {
      access(filePath).write("hello world\nfoo bar\nhello again")
      val matches = grep(filePath, "hello")
      assertEquals(matches.length, 2)
      assertEquals(matches(0).lineNumber, 1)
      assertEquals(matches(0).line, "hello world")
      assertEquals(matches(1).lineNumber, 3)
    }
  }

  test("grepRecursive searches across files with glob filter") {
    val dirPath = tmpDir.toString
    val aPath = tmpDir.resolve("a.scala").toString
    val bPath = tmpDir.resolve("sub/b.scala").toString
    val cPath = tmpDir.resolve("c.txt").toString
    requestFileSystem(dirPath) {
      val fs = summon[FileSystem]
      fs.access(aPath).write("val x = 1\nval y = 2")
      fs.access(bPath).write("val x = 10")
      fs.access(cPath).write("val x = ignored")
      val matches = grepRecursive(dirPath, "val x", "*.scala")
      assertEquals(matches.length, 2)
      assert(matches.forall(_.line.contains("val x")))
    }
  }

  test("find locates files by glob") {
    val dirPath = tmpDir.toString
    val aPath = tmpDir.resolve("one.scala").toString
    val bPath = tmpDir.resolve("sub/two.scala").toString
    val cPath = tmpDir.resolve("three.txt").toString
    requestFileSystem(dirPath) {
      val fs = summon[FileSystem]
      fs.access(aPath).write("")
      fs.access(bPath).write("")
      fs.access(cPath).write("")
      val found = find(dirPath, "*.scala")
      assertEquals(found.length, 2)
      assert(found.forall(_.endsWith(".scala")))
    }
  }

  test("walkDir lists entries recursively with metadata") {
    val dirPath = tmpDir.toString
    val filePath = tmpDir.resolve("dir1/file.txt").toString
    requestFileSystem(dirPath) {
      val fs = summon[FileSystem]
      fs.access(filePath).write("content")
      val entries = fs.access(dirPath).walk()
      val dirs = entries.filter(_.isDirectory)
      val files = entries.filter(!_.isDirectory)
      assert(dirs.exists(_.name == "dir1"))
      assert(files.exists(_.name == "file.txt"))
    }
  }

  test("exec runs allowed command and captures output") {
    requestExecPermission(Set("echo")) {
      val result = exec("echo", List("hello", "world"))
      assertEquals(result.exitCode, 0)
      // Output capture is byte-exact: the trailing newline is preserved.
      assertEquals(result.stdout, "hello world\n")
    }
  }

  test("exec caps captured output at 8 MiB per stream") {
    // `seq 1 2000000` emits ~15 MB; beyond 8 MiB the output is drained (so the
    // process never blocks) but discarded, with a truncation marker appended.
    requestExecPermission(Set("seq")) {
      val result = exec("seq", List("1", "2000000"), timeoutMs = 60000)
      assertEquals(result.exitCode, 0)
      assert(
        result.stdout.endsWith("...[truncated: output exceeded 8 MiB cap]..."),
        s"expected truncation marker, got tail: ${result.stdout.takeRight(80)}"
      )
      assert(result.stdout.length <= 8 * 1024 * 1024 + 100)
    }
  }

  test("exec rejects disallowed command") {
    requestExecPermission(Set("echo")) {
      val ex = intercept[SecurityException] {
        exec("rm", List("-rf", "/"))
      }
      assert(ex.getMessage.nn.contains("Access denied"))
    }
  }
  test("classified path enforcement on real file system") {
    val secretDir = tmpDir.resolve("secret")
    Files.createDirectories(secretDir)
    val classifiedInterface: Interface^ = new InterfaceImpl(
      """{"strictMode": false, "classifiedPaths": ["secret"], "allowedRoots": ["/"]}"""
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    import classifiedInterface.*

    requestFileSystem(tmpDir.toString) {
      // Normal file works
      val pub = access(tmpDir.resolve("public.txt").toString)
      pub.write("public data")
      assertEquals(pub.read(), "public data")
      assert(!pub.isClassified)

      // Classified file: normal ops blocked
      val sec = access(secretDir.resolve("data.txt").toString)
      assert(sec.isClassified)
      intercept[SecurityException] { sec.write("nope") }
      intercept[SecurityException] { sec.read() }

      // Classified ops work
      sec.writeClassified(classifiedInterface.classify("top-secret"))
      val content = sec.readClassified()
      assertEquals(content.toString, "Classified(***)")

      // readClassified on non-classified throws
      intercept[SecurityException] { pub.readClassified() }
    }
  }

  test("mkdir creates directory and parent directories") {
    requestFileSystem(tmpDir.toString) {
      val dir = access(tmpDir.resolve("a/b/c").toString)
      assert(!dir.exists)
      dir.mkdir()
      assert(dir.exists)
      assert(dir.isDirectory)
      assert(access(tmpDir.resolve("a").toString).isDirectory)
      assert(access(tmpDir.resolve("a/b").toString).isDirectory)
    }
  }

  test("secureOutput: classified println masks main stream, reveals in secure file") {
    val secureFile = tmpDir.resolve("secure.log")
    val secureInterface: Interface^ = new InterfaceImpl(
      io.circe.Json.obj("secureOutput" -> io.circe.Json.fromString(secureFile.toString)).noSpaces
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    given (IOCapability^{}) = null.asInstanceOf[IOCapability]

    val mainBuf = new java.io.ByteArrayOutputStream()
    scala.Console.withOut(new java.io.PrintStream(mainBuf, true, "UTF-8")) {
      secureInterface.println(secureInterface.classify("top-secret"))
      secureInterface.println("plain message")
      secureInterface.print(secureInterface.classify("hidden"))
      secureInterface.println()
      secureInterface.printf("score=%d name=%s%n", 42, secureInterface.classify("alice"))
    }

    val mainOut = mainBuf.toString("UTF-8")
    val secureOut = Files.readString(secureFile).nn

    // Main stream never leaks classified content
    assert(!mainOut.contains("top-secret"), s"main stream leaked: $mainOut")
    assert(!mainOut.contains("hidden"))
    assert(!mainOut.contains("alice"))
    assert(mainOut.contains("Classified(***)"))
    assert(mainOut.contains("plain message"))
    assert(mainOut.contains("score=42 name=Classified(***)"))

    // Secure sink sees the unwrapped content
    assert(secureOut.contains("top-secret"))
    assert(secureOut.contains("hidden"))
    assert(secureOut.contains("plain message"))
    assert(secureOut.contains("score=42 name=alice"))
  }

  test("secureOutput: the writer for a path is cached, not reopened per instance") {
    // A fresh InterfaceImpl is built on every REPL init; opening a new
    // FileOutputStream each time would leak a file descriptor per execution.
    // The writer must be shared across instances for the same path.
    val p = tmpDir.resolve("shared-secure.log").toString
    val w1 = InterfaceImpl.secureWriterFor(p)
    val w2 = InterfaceImpl.secureWriterFor(p)
    assert(w1 eq w2, "secureOutput PrintStream should be reused for the same path")
    val w3 = InterfaceImpl.secureWriterFor(tmpDir.resolve("other-secure.log").toString)
    assert(w1 ne w3, "different paths should get different writers")
  }

  test("secureOutput: when unset, println behaves like Predef and only writes to main") {
    val mainBuf = new java.io.ByteArrayOutputStream()
    scala.Console.withOut(new java.io.PrintStream(mainBuf, true, "UTF-8")) {
      println(classify("top-secret"))
      println("visible")
    }

    val mainOut = mainBuf.toString("UTF-8")
    assert(mainOut.contains("Classified(***)"))
    assert(!mainOut.contains("top-secret"))
    assert(mainOut.contains("visible"))
  }

  test("mkdir on existing directory is idempotent") {
    requestFileSystem(tmpDir.toString) {
      val dir = access(tmpDir.resolve("existing").toString)
      dir.mkdir()
      dir.mkdir() // should not throw
      assert(dir.isDirectory)
    }
  }

  test("calling foreach(println) on the result of grepRecursive") {
    val dirPath = tmpDir.toString
    requestFileSystem(dirPath) {
      access(tmpDir.resolve("a.txt").toString).write("line one\nother\nline three")
      access(tmpDir.resolve("sub/b.txt").toString).write("line two")
      access(tmpDir.resolve("c.md").toString).write("line ignored by glob")

      val matches = grepRecursive(dirPath, "line", "*.txt")
        .map(m => s"${m.file}:${m.lineNumber}: ${m.line}")
      assertEquals(matches.length, 3)
      assert(matches.forall(_.contains("line")))

      // Exercise foreach(println) on the result: println requires IOCapability and
      // must type-check under capture checking. Capture stdout to verify it ran.
      val buf = new java.io.ByteArrayOutputStream()
      scala.Console.withOut(new java.io.PrintStream(buf, true, "UTF-8")) {
        matches.foreach(println)
      }
      val out = buf.toString("UTF-8")
      assert(out.contains("a.txt"), s"expected a.txt in output: $out")
      assert(out.contains("b.txt"), s"expected b.txt in output: $out")
      assert(!out.contains("c.md"), s"c.md should be excluded by the *.txt glob: $out")
    }
  }

  // ── allowedRoots: server-configured outer bound on requestFileSystem ──

  private def boundedInterface(roots: Set[String]): Interface^ =
    new InterfaceImpl(
      io.circe.Json.obj(
        "strictMode" -> io.circe.Json.fromBoolean(false),
        "allowedRoots" -> io.circe.Json.fromValues(roots.map(io.circe.Json.fromString))
      ).noSpaces
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }

  test("allowedRoots permits a root nested within the bound") {
    val api = boundedInterface(Set(tmpDir.toString))
    val sub = tmpDir.resolve("sub")
    Files.createDirectories(sub)
    api.requestFileSystem(sub.toString) {
      api.access(sub.resolve("f.txt").toString).write("ok")
    }
  }

  test("allowedRoots permits the bound root itself") {
    val api = boundedInterface(Set(tmpDir.toString))
    api.requestFileSystem(tmpDir.toString) {
      api.access(tmpDir.resolve("g.txt").toString).write("ok")
    }
  }

  test("allowedRoots denies a root outside the bound") {
    val allowed = tmpDir.resolve("allowed")
    Files.createDirectories(allowed)
    val api = boundedInterface(Set(allowed.toString))
    val ex = intercept[SecurityException] {
      api.requestFileSystem("/etc") { api.access("/etc/hosts").read() }
    }
    assert(ex.getMessage.nn.contains("not within any allowed root"))
  }

  test("allowedRoots denies a sibling that merely shares a name prefix") {
    // `/tmp/allowed-evil` must not pass a `/tmp/allowed` bound: the check is
    // path-component-wise (startsWith on Path), not string prefix.
    val allowed = tmpDir.resolve("allowed")
    val evil = tmpDir.resolve("allowed-evil")
    Files.createDirectories(allowed)
    Files.createDirectories(evil)
    val api = boundedInterface(Set(allowed.toString))
    val ex = intercept[SecurityException] {
      api.requestFileSystem(evil.toString) { api.access(evil.resolve("x").toString).write("no") }
    }
    assert(ex.getMessage.nn.contains("not within any allowed root"))
  }

  test("allowedRoots denies a symlink root that escapes the bound") {
    val allowed = tmpDir.resolve("allowed")
    Files.createDirectories(allowed)
    val outside = Files.createTempDirectory("bound-outside")
    try
      // A symlink inside the allowed root that points outside it must be denied,
      // because the bound check resolves symlinks before comparing.
      val link = allowed.resolve("escape")
      Files.createSymbolicLink(link, outside)
      val api = boundedInterface(Set(allowed.toString))
      val ex = intercept[SecurityException] {
        api.requestFileSystem(link.toString) { api.access(link.resolve("x").toString).read() }
      }
      assert(ex.getMessage.nn.contains("not within any allowed root"))
    finally
      Files.deleteIfExists(outside)
  }

  test("no allowedRoots configured defaults to the current working directory") {
    // With allowedRoots unset, the bound defaults to the process CWD (fail
    // closed). A fresh interface with no allowedRoots must allow the CWD but
    // deny a temp dir that lives outside it.
    val api: Interface^ = new InterfaceImpl("{}") {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    val cwd = java.nio.file.Paths.get(System.getProperty("user.dir").nn)
    assertEquals(api.requestFileSystem(cwd.toString) { 1 }, 1)   // CWD is permitted
    // The parent of CWD is always outside the default bound, regardless of where
    // the test runner sets the working directory.
    val parent = cwd.getParent.nn
    val ex = intercept[SecurityException] {
      api.requestFileSystem(parent.toString) { api.access(parent.resolve("x").toString).read() }
    }
    assert(ex.getMessage.nn.contains("not within any allowed root"))
  }

  // ── grepRecursive: classified files are skipped, not fatal ──

  test("grepRecursive silently skips classified files and still returns other matches") {
    val secretDir = tmpDir.resolve("secret")
    Files.createDirectories(secretDir)
    val classifiedInterface: Interface^ = new InterfaceImpl(
      """{"strictMode": false, "classifiedPaths": ["secret"], "allowedRoots": ["/"]}"""
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    classifiedInterface.requestFileSystem(tmpDir.toString) {
      classifiedInterface.access(tmpDir.resolve("public.txt").toString).write("needle visible")
      classifiedInterface.access(secretDir.resolve("hidden.txt").toString)
        .writeClassified(classifiedInterface.classify("needle hidden"))
      // Must not throw, and must not read the classified file's content.
      val matches = classifiedInterface.grepRecursive(tmpDir.toString, "needle")
      assertEquals(matches.length, 1)
      assert(matches.head.file.endsWith("public.txt"), s"unexpected matches: $matches")
    }
  }

  test("grep still fails closed on a classified file") {
    val secretDir = tmpDir.resolve("secret")
    Files.createDirectories(secretDir)
    val classifiedInterface: Interface^ = new InterfaceImpl(
      """{"strictMode": false, "classifiedPaths": ["secret"], "allowedRoots": ["/"]}"""
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    classifiedInterface.requestFileSystem(tmpDir.toString) {
      classifiedInterface.access(secretDir.resolve("hidden.txt").toString)
        .writeClassified(classifiedInterface.classify("needle hidden"))
      intercept[SecurityException] {
        classifiedInterface.grep(secretDir.resolve("hidden.txt").toString, "needle")
      }
    }
  }

  // ── classifiedWrite gate ──────────────────────────────────────

  test("classifiedWrite = false blocks writeClassified at both interface and entry level") {
    val secretDir = tmpDir.resolve("secret")
    Files.createDirectories(secretDir)
    val gated: Interface^ = new InterfaceImpl(
      """{"strictMode": false, "classifiedPaths": ["secret"], "allowedRoots": ["/"], "classifiedWrite": false}"""
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns, classifiedWrite = false)
    }
    gated.requestFileSystem(tmpDir.toString) {
      val ex1 = intercept[SecurityException] {
        gated.writeClassified(secretDir.resolve("a.txt").toString, gated.classify("x"))
      }
      assert(ex1.getMessage.nn.contains("classifiedWrite"), ex1.getMessage)
      // The entry-level path must be gated too, not just the interface-level one.
      val ex2 = intercept[SecurityException] {
        gated.access(secretDir.resolve("b.txt").toString).writeClassified(gated.classify("x"))
      }
      assert(ex2.getMessage.nn.contains("classifiedWrite"), ex2.getMessage)
    }
  }

  test("classifiedWrite defaults to true when unset") {
    val secretDir = tmpDir.resolve("secret")
    Files.createDirectories(secretDir)
    val open: Interface^ = new InterfaceImpl(
      """{"strictMode": false, "classifiedPaths": ["secret"], "allowedRoots": ["/"]}"""
    ) {
      override def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        new RealFileSystem(root, filter, classifiedPatterns)
    }
    open.requestFileSystem(tmpDir.toString) {
      open.writeClassified(secretDir.resolve("ok.txt").toString, open.classify("data"))
      assertEquals(Files.readString(secretDir.resolve("ok.txt")).nn, "data")
    }
  }

  // ── secureOutput hardening ────────────────────────────────────

  test("secureOutput: different spellings of the same file share one writer") {
    val w1 = InterfaceImpl.secureWriterFor(tmpDir.resolve("spell-secure.log").toString)
    val w2 = InterfaceImpl.secureWriterFor(tmpDir.resolve("sub/../spell-secure.log").toString)
    assert(w1 eq w2, "canonical paths should share a single PrintStream")
  }

  test("secureOutput: newly created file is owner-only on POSIX filesystems") {
    val secureFile = tmpDir.resolve("posix-secure.log")
    InterfaceImpl.secureWriterFor(secureFile.toString)
    try
      val perms = Files.getPosixFilePermissions(secureFile)
      assertEquals(
        perms,
        java.nio.file.attribute.PosixFilePermissions.fromString("rw-------")
      )
    catch case _: UnsupportedOperationException => () // non-POSIX filesystem: nothing to check
  }

  // --- Compile-time capability leak examples ---
  // The following code would fail to compile with capture checking enabled,
  // because the capability `fs` cannot escape the scope of `requestFileSystem`.
  //
  //   var leaked: FileSystem = null
  //   requestFileSystem("/tmp") {
  //     leaked = summon[FileSystem]  // ERROR: local reference leaks into outer capture set
  //   }
  //
  // Similarly, storing the capability in a closure that escapes:
  //
  //   var escapedOp: () => String = null
  //   requestFileSystem("/tmp") {
  //     escapedOp = () => readFile("/tmp/secret.txt")
  //     // ERROR: the capability is captured but the closure type () => String
  //     // does not account for it in its capture set
  //   }
