package tacit.executor

import java.nio.charset.StandardCharsets.UTF_8

/** Unit tests for the bounded REPL output capture: output beyond the cap is
  * discarded (never buffered), the truncation flag is set, and writes never
  * throw into the evaluation path. */
class OutputCaptureSuite extends munit.FunSuite:

  test("output within the limit is captured fully"):
    val out = ManagedRepl.BoundedOutputStream(16)
    out.write("hello".getBytes(UTF_8))
    assert(!out.truncated)
    assertEquals(out.capturedString, "hello")

  test("output beyond the limit is discarded and flagged"):
    val out = ManagedRepl.BoundedOutputStream(8)
    out.write("0123456789abcdef".getBytes(UTF_8))
    assert(out.truncated)
    assertEquals(out.capturedString, "01234567")

  test("single-byte writes beyond the limit are dropped"):
    val out = ManagedRepl.BoundedOutputStream(2)
    out.write('a'.toInt)
    out.write('b'.toInt)
    out.write('c'.toInt)
    assert(out.truncated)
    assertEquals(out.capturedString, "ab")

  test("further writes after truncation never throw or grow the buffer"):
    val out = ManagedRepl.BoundedOutputStream(4)
    val big = new Array[Byte](1024 * 1024)
    out.write(big, 0, big.length)
    assert(out.truncated)
    assertEquals(out.capturedString.length, 4)
    // Still safe to keep writing.
    out.write(big, 0, big.length)
    assertEquals(out.capturedString.length, 4)

  test("resetCapture clears content and the truncation flag"):
    val out = ManagedRepl.BoundedOutputStream(4)
    out.write("abcdef".getBytes(UTF_8))
    assert(out.truncated)
    out.resetCapture()
    assert(!out.truncated)
    assertEquals(out.capturedString, "")
    out.write("xy".getBytes(UTF_8))
    assert(!out.truncated)
    assertEquals(out.capturedString, "xy")

  test("truncation marker mentions the cap"):
    assert(ManagedRepl.TruncationMarker.contains("truncated"))
