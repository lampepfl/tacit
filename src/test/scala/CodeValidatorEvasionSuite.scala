import tacit.executor.{CodeValidator, ScalaExecutor}
import tacit.core.{Context, Config}

/** Regression tests for validator-evasion techniques found during the security
  * audit. Each technique reached the compiler with a forbidden construct intact
  * (caught only by safe mode); with safe mode off they were working sandbox
  * escapes. These assert the text validator itself now blocks them.
  */
class CodeValidatorEvasionSuite extends munit.FunSuite:
  private def ids(code: String): List[String] = CodeValidator.validate(code).map(_.ruleId)

  // ── Multi-line dot-split: a dotted token broken across a newline ──────────

  test("split: import java.<nl>io.File"):
    assert(ids("import java.\n  io.File").contains("file-io-java"))
  test("split: trailing-dot System.getProperty"):
    assert(ids("System.\ngetProperty(\"user.home\")").contains("sys-getprop"))
  test("split: leading-dot System.exit"):
    assert(ids("val z = 1\nSystem\n.exit(0)").contains("sys-exit"))
  test("split: Runtime.<nl>getRuntime"):
    assert(ids("Runtime.\ngetRuntime.exec(\"id\")").contains("proc-runtime"))
  test("split: caps.<nl>unsafe"):
    assert(ids("import caps.\nunsafe.unsafeAssumePure").contains("cc-unsafe-caps"))
  test("split: three-line scala.sys.process"):
    assert(ids("import scala.\n  sys.\n  process._").contains("proc-scala"))
  test("split: extra whitespace java.nio"):
    assert(ids("import java   .\n      nio.file.Files").contains("file-io-nio"))

  // ── String interpolation: ${...} is executed code, must be analyzed ───────

  test("interp: System.getProperty"):
    assert(ids("""val x = s"${System.getProperty("user.home")}"""").contains("sys-getprop"))
  test("interp: java.lang.Runtime"):
    assert(ids("""val x = s"${java.lang.Runtime.getRuntime}"""").contains("proc-runtime"))
  test("interp: new java.io.File"):
    assert(ids("""val x = s"${new java.io.File("/x")}"""").contains("file-io-java"))
  test("interp: triple-quoted"):
    assert(ids("val x = s\"\"\"${System.exit(0)}\"\"\"").contains("sys-exit"))
  test("interp: f-interpolator"):
    assert(ids("""val x = f"${System.exit(0)}%s"""").contains("sys-exit"))
  test("interp: dot-split inside ${}"):
    assert(ids("val x = s\"${System.\nexit(0)}\"").contains("sys-exit"))

  // ── scala.sys.* re-exposes System.* hooks (scala is auto-imported) ────────

  test("sys.process import"):
    assert(ids("import sys.process._").contains("proc-scala"))
  test("scala.sys.process import"):
    assert(ids("import scala.sys.process.*").contains("proc-scala"))
  test("sys.exit"):
    assert(ids("sys.exit(0)").contains("sys-scala"))
  test("sys.env"):
    assert(ids("sys.env.get(\"PATH\")").contains("sys-scala"))
  test("sys.props"):
    assert(ids("sys.props(\"user.home\")").contains("sys-scala"))
  test("sys.runtime"):
    assert(ids("sys.runtime.halt(0)").contains("sys-scala"))

  // ── False-positive guards: legitimate code must still pass ────────────────

  test("no-fp: leading-dot method chaining"):
    assertEquals(ids("List(1,2,3)\n  .map(_ * 2)\n  .filter(_ > 2)\n  .sum"), Nil)
  test("no-fp: trailing-dot method chaining"):
    assertEquals(ids("val r = List(1,2,3).\n  map(_ * 2).\n  filter(_ > 2)"), Nil)
  test("no-fp: forbidden token quoted inside interpolation expr"):
    assertEquals(ids("""val x = s"${ identity("java.io.File") }""""), Nil)
  test("no-fp: forbidden token in interpolation literal text"):
    assertEquals(ids("""val x = s"java.io.File is just text ${1 + 1}""""), Nil)
  test("no-fp: dollar in a plain (non-interpolated) string"):
    assertEquals(ids("""val x = "$java.io is literal""""), Nil)
  test("no-fp: a user value named like sys is not scala.sys"):
    assertEquals(ids("val mysys = obj; mysys.exit"), Nil)
  test("no-fp: sys.error (a plain throw helper) is allowed"):
    assertEquals(ids("""sys.error("boom")"""), Nil)

  // ── End-to-end: with safe mode OFF these were real escapes; the validator
  //    must now reject them before the code is ever compiled or run. ─────────

  private def rejected(code: String): tacit.executor.ExecutionResult =
    given Context = Context(Config(safeMode = false), None)
    ScalaExecutor.execute(code)

  test("e2e (no-safe-mode): interpolation getProperty is rejected, not executed"):
    val r = rejected("""val x = s"${System.getProperty("user.home")}"; x""")
    assert(!r.success)
    assert(r.error.exists(_.contains("sys-getprop")), s"got: ${r.error}")
    assert(!r.output.contains(System.getProperty("user.home").nn))

  test("e2e (no-safe-mode): sys.props is rejected, not executed"):
    val r = rejected("""sys.props("user.home")""")
    assert(!r.success)
    assert(r.error.exists(_.contains("sys-scala")), s"got: ${r.error}")

  test("e2e (no-safe-mode): multi-line import split is rejected"):
    val r = rejected("import java.\n  io.File")
    assert(!r.success)
    assert(r.error.exists(_.contains("file-io-java")), s"got: ${r.error}")

  test("e2e: legitimate interpolation and chaining still run"):
    given Context = Context(Config(), None)
    val r = ScalaExecutor.execute("""val n = 3; val s = s"n=${n * 2}"; s""")
    assert(r.success, s"got: ${r.error.getOrElse(r.output)}")
    assert(r.output.contains("n=6"))
