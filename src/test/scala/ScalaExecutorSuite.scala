import tacit.executor.{ScalaExecutor, SessionManager}
import tacit.core.{Context, Config}

class ScalaExecutorSuite extends munit.FunSuite:
  given Context = Context(Config(), None)

  // ── Basic execution ────────────────────────────────────────────────

  test("execute simple expression"):
    val result = ScalaExecutor.execute("1 + 1")
    assert(result.success)
    assert(result.output.contains("2"))

  test("execute println"):
    val result = ScalaExecutor.execute("""println("Hello, World!")""")
    assert(result.success)
    assert(result.output.contains("Hello, World!"))

  test("execute val definition"):
    val result = ScalaExecutor.execute("val x = 42\nprintln(x)")
    assert(result.success)
    assert(result.output.contains("42"))

  test("execute function definition and call"):
    val result = ScalaExecutor.execute("""
      def add(a: Int, b: Int): Int = a + b
      add(2, 3)
    """)
    assert(result.success)
    assert(result.output.contains("5"))

  test("handle syntax error"):
    val result = ScalaExecutor.execute("val x = def")
    assert(!result.success)

  test("use scala.collection.List"):
    val result = ScalaExecutor.execute("List(1, 2, 3).map(_ * 2)")
    assert(result.success)
    assert(result.output.contains("List(2, 4, 6)"))

  test("foreach println on List of String"):
    val result = ScalaExecutor.execute("""List("hello", "world").foreach(println)""")
    assert(result.success)
    assert(result.output.contains("hello"))
    assert(result.output.contains("world"))

  test("use scala.collection.Map"):
    val result = ScalaExecutor.execute("""Map("a" -> 1, "b" -> 2).values.toList.sorted""")
    assert(result.success)
    assert(result.output.contains("List(1, 2)"))

  // ── Validation ──────────────────────────────────────────────────

  test("reject java.io.File"):
    val result = ScalaExecutor.execute("""
      import java.io.File
      val f = new File("/tmp")
      f.isDirectory
    """)
    assert(!result.success)
    assert(result.error.exists(_.contains("file-io-java")))

  test("use java.time"):
    val result = ScalaExecutor.execute("""
      import java.time.LocalDate
      val d = LocalDate.of(2025, 1, 1)
      d.getYear
    """)
    assert(result.success)
    assert(result.output.contains("2025"))

  test("use scala.util.Try"):
    val result = ScalaExecutor.execute("""
      import scala.util.Try
      Try("123".toInt).isSuccess
    """)
    assert(result.success)
    assert(result.output.contains("true"))

  test("reject scala.io.Source"):
    val result = ScalaExecutor.execute("""
      import scala.io.Source
      Source.fromString("hello\nworld").getLines().toList
    """)
    assert(!result.success)
    assert(result.error.exists(_.contains("file-io-scala")))

  // ── Error handling and edge cases ────────────────────────────────

  test("runtime exception is caught and reported"):
    val result = ScalaExecutor.execute("""throw new RuntimeException("boom")""")
    assert(!result.success || result.output.contains("RuntimeException") || result.error.isDefined)

  test("empty code does not crash"):
    val result = ScalaExecutor.execute("")
    // Should not throw; success with empty or minimal output
    assert(result != null)

  test("whitespace-only code does not crash"):
    val result = ScalaExecutor.execute("   \n\n  ")
    assert(result != null)

  // ── Language features ───────────────────────────────────────────

  test("multiline code with class definition"):
    val result = ScalaExecutor.execute("""
      case class Point(x: Int, y: Int):
        def distTo(other: Point): Double =
          math.sqrt(math.pow(x - other.x, 2) + math.pow(y - other.y, 2))
      Point(0, 0).distTo(Point(3, 4))
    """)
    assert(result.success, s"execution failed: ${result.error.getOrElse(result.output)}")
    assert(result.output.contains("5.0"))

  test("code with type error returns success=false"):
    val result = ScalaExecutor.execute("""val x: Int = "hello"""")
    assert(!result.success)

  test("code producing large output completes"):
    val result = ScalaExecutor.execute("(1 to 500).toList")
    assert(result.success)
    assert(result.output.contains("List(1, 2, 3"))

  test("pattern matching expression"):
    val result = ScalaExecutor.execute("""
      val x: Any = 42
      x match
        case i: Int => s"int: $i"
        case s: String => s"str: $s"
        case _ => "other"
    """)
    assert(result.success)
    assert(result.output.contains("int: 42"))

  test("higher-order functions"):
    val result = ScalaExecutor.execute("""
      List(1, 2, 3, 4, 5).filter(_ % 2 == 0).map(_ * 10)
    """)
    assert(result.success)
    assert(result.output.contains("List(20, 40)"))

  // ── Configuration ───────────────────────────────────────────────

  test("unwrapped mode executes code directly"):
    given Context = Context(Config(wrappedCode = false), None)
    val result = ScalaExecutor.execute("val unwrapped = 99\nunwrapped")
    assert(result.success)
    assert(result.output.contains("99"))

  test("for comprehension"):
    val result = ScalaExecutor.execute("""
      for
        x <- List(1, 2, 3)
        y <- List(10, 20)
      yield x * y
    """)
    assert(result.success)
    assert(result.output.contains("List(10, 20, 20, 40, 30, 60)"))

  // ── REPL command allowlist ──────────────────────────────────────

  private def assertCommandRejected(cmd: String): Unit =
    val result = ScalaExecutor.execute(cmd)
    assert(!result.success, s"'$cmd' should be rejected")
    assert(result.error.exists(_.contains("Only :type, :doc, and :imports")),
      s"'$cmd' error should mention the allowlist, got: ${result.error}")

  test("rejects :quit"):
    assertCommandRejected(":quit")

  test("rejects :q"):
    assertCommandRejected(":q")

  test("rejects :settings"):
    assertCommandRejected(":settings")

  test("rejects :dep"):
    assertCommandRejected(":dep com.example::lib:1.0")

  test("rejects :sh"):
    assertCommandRejected(":sh echo hello")

  test("rejects :load"):
    assertCommandRejected(":load /tmp/evil.scala")

  test("rejects :reset"):
    assertCommandRejected(":reset")

  test("rejects :help"):
    assertCommandRejected(":help")

  // ── Allowed REPL commands ────────────────────────────────────────

  // TODO: try to enable this test after https://github.com/scala/scala3/pull/25789
  // test(":type returns type information"):
  //   val manager = new SessionManager
  //   val sid = manager.createSession()
  //   manager.executeInSession(sid, "val x = 42")
  //   val result = manager.executeInSession(sid, ":type x")
  //   assert(result.success, s"':type x' should succeed, got error: ${result.error}")
  //   assert(result.output.contains("Int"), s"':type x' should mention Int, got: ${result.output}")
