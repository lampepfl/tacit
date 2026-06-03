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

  private val interface: Interface^{} = new InterfaceImpl("{}") {
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
      assertEquals(result.stdout, "hello world")
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
      """{"strictMode": false, "classifiedPaths": ["secret"]}"""
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
