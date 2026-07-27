import tacit.executor.{ScalaExecutor, SessionManager}
import tacit.core.{Context, Config}

/** Tests for the optional wall-clock execution timeout (`executionTimeoutMs`).
  * The timeout runs evaluation on a worker thread; a timed-out run must return
  * an error to the client without corrupting later session state. `Thread.sleep`
  * stands in for a slow-but-interruptible operation; the validator allows it
  * (it is not `new Thread`). Safe mode is disabled here only because safe mode
  * would reject `Thread.sleep` as impure at compile time; the timeout mechanism
  * itself is independent of safe mode.
  */
class ExecutionTimeoutSuite extends munit.FunSuite:
  private def ctx(limitMs: Long): Context =
    Context(Config(safeMode = false, executionTimeoutMs = Some(limitMs)), None)

  test("execution within the timeout succeeds and returns its value"):
    val r = ScalaExecutor.execute("1 + 1")(using ctx(10000))
    assert(r.success, s"got: ${r.error.getOrElse(r.output)}")
    assert(r.output.contains("2"))

  test("a slow execution times out with an error"):
    val r = ScalaExecutor.execute("Thread.sleep(20000); 42")(using ctx(500))
    assert(!r.success)
    assert(r.error.exists(_.contains("timed out")), s"got: ${r.error}")
    assert(!r.output.contains("42"))

  test("a timed-out statement does not corrupt session state"):
    given Context = ctx(800)
    val sm = new SessionManager
    val sid = sm.createSession().fold(err => fail(s"session creation failed: $err"), identity)
    try
      assert(sm.executeInSession(sid, "val x = 41").success)
      val timedOut = sm.executeInSession(sid, "Thread.sleep(20000); x")
      assert(!timedOut.success, "expected the slow statement to time out")
      // The session must still be usable, with `x` intact from before the timeout.
      val after = sm.executeInSession(sid, "x + 1")
      assert(after.success, s"session broken after timeout: ${after.error.getOrElse(after.output)}")
      assert(after.output.contains("42"))
    finally sm.deleteSession(sid)
