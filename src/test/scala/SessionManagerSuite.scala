import tacit.executor.SessionManager
import tacit.core.{Context, Config}

class SessionManagerSuite extends munit.FunSuite:
  given Context = Context(Config(), None)

  private def newSession(manager: SessionManager): String =
    manager.createSession().fold(err => fail(s"session creation failed: $err"), identity)

  // ── CRUD ───────────────────────────────────────────────────────────

  test("create and list sessions"):
    val manager = new SessionManager
    val sessionId = newSession(manager)
    assert(sessionId.nonEmpty)
    assert(manager.listSessions().contains(sessionId))

  test("delete session"):
    val manager = new SessionManager
    val sessionId = newSession(manager)
    assert(manager.deleteSession(sessionId))
    assert(!manager.listSessions().contains(sessionId))

  test("delete non-existent session"):
    val manager = new SessionManager
    assert(!manager.deleteSession("non-existent-id"))

  test("execute in session maintains state"):
    val manager = new SessionManager
    val sessionId = newSession(manager)

    // Define a variable
    val result1 = manager.executeInSession(sessionId, "val x = 42")
    assert(result1.success)

    // Use the variable in next execution
    val result2 = manager.executeInSession(sessionId, "x * 2")
    assert(result2.output.contains("84"))

  test("execute in non-existent session returns error"):
    val manager = new SessionManager
    val result = manager.executeInSession("non-existent-id", "1 + 1")
    assert(!result.success)
    assert(result.error.exists(_.contains("not found")))

  // ── Isolation and lifecycle ──────────────────────────────────────

  test("sessions are isolated from each other"):
    val manager = new SessionManager
    val s1 = newSession(manager)
    val s2 = newSession(manager)
    manager.executeInSession(s1, "val x = 1")
    manager.executeInSession(s2, "val x = 2")
    val r1 = manager.executeInSession(s1, "x")
    val r2 = manager.executeInSession(s2, "x")
    assert(r1.output.contains("1"))
    assert(r2.output.contains("2"))

  test("execute in deleted session returns error"):
    val manager = new SessionManager
    val sessionId = newSession(manager)
    manager.deleteSession(sessionId)
    val result = manager.executeInSession(sessionId, "1 + 1")
    assert(!result.success)
    assert(result.error.exists(_.contains("not found")))

  test("re-delete already deleted session returns false"):
    val manager = new SessionManager
    val sessionId = newSession(manager)
    assert(manager.deleteSession(sessionId))
    assert(!manager.deleteSession(sessionId))

  test("multiple sessions listed correctly"):
    val manager = new SessionManager
    val ids = (1 to 3).map(_ => newSession(manager)).toSet
    val listed = manager.listSessions().toSet
    assertEquals(listed, ids)

  test("delete one session does not affect others"):
    val manager = new SessionManager
    val s1 = newSession(manager)
    val s2 = newSession(manager)
    manager.executeInSession(s1, "val a = 10")
    manager.executeInSession(s2, "val b = 20")
    manager.deleteSession(s1)
    val result = manager.executeInSession(s2, "b")
    assert(result.success)
    assert(result.output.contains("20"))
    assert(!manager.listSessions().contains(s1))
    assert(manager.listSessions().contains(s2))

  // ── State persistence ───────────────────────────────────────────

  test("session preserves imports across calls"):
    val manager = new SessionManager
    val sid = newSession(manager)
    manager.executeInSession(sid, "import java.time.LocalDate")
    val result = manager.executeInSession(sid, "LocalDate.of(2025, 1, 1).getYear")
    assert(result.success)
    assert(result.output.contains("2025"))

  test("session preserves function definitions"):
    val manager = new SessionManager
    val sid = newSession(manager)
    manager.executeInSession(sid, "def double(n: Int): Int = n * 2")
    val result = manager.executeInSession(sid, "double(21)")
    assert(result.success)
    assert(result.output.contains("42"))

  test("session handles compile error without corrupting state"):
    val manager = new SessionManager
    val sid = newSession(manager)
    val r1 = manager.executeInSession(sid, "val good = 42")
    assert(r1.success)
    val r2 = manager.executeInSession(sid, "val bad: Int = \"not an int\"")
    assert(!r2.success)
    val r3 = manager.executeInSession(sid, "good")
    assert(r3.success)
    assert(r3.output.contains("42"))

  // ── Session cap ─────────────────────────────────────────────────

  test("session cap rejects creation beyond the limit"):
    val manager = new SessionManager(maxSessions = 2)
    val s1 = newSession(manager)
    newSession(manager)
    val third = manager.createSession()
    assert(third.isLeft, s"expected cap rejection but got: $third")
    assert(third.left.toOption.exists(_.contains("Session limit reached")),
      s"expected a clear cap error but got: $third")
    // Deleting a session frees a slot again.
    assert(manager.deleteSession(s1))
    assert(manager.createSession().isRight)
