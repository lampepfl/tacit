package tacit.library

import language.experimental.captureChecking

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*

object ProcessOps:
  /** Maximum bytes captured per output stream. Output beyond the cap is
   *  drained (so the process never blocks on a full pipe) but discarded, so a
   *  `yes`-style process cannot OOM the server. */
  private val MaxStreamBytes = 8 * 1024 * 1024 // 8 MiB

  private val TruncationMarker = "\n...[truncated: output exceeded 8 MiB cap]..."

  /** How long to wait for the drainer threads after the process exits. A
   *  grandchild that inherited the pipes can keep them open after the direct
   *  child exits; without a bound the joins below would hang `exec` forever. */
  private val DrainJoinTimeoutMs = 5000L

  /** Drains an input stream into a string on the current thread, byte-exact
   *  (trailing newlines preserved), capped at [[MaxStreamBytes]]. */
  private def drainStream(stream: java.io.InputStream): String =
    val out = java.io.ByteArrayOutputStream()
    val buf = new Array[Byte](8192)
    var captured = 0
    var truncated = false
    try
      var n = stream.read(buf)
      while n >= 0 do
        val room = MaxStreamBytes - captured
        if room > 0 then
          val keep = math.min(room, n)
          out.write(buf, 0, keep)
          captured += keep
          if keep < n then truncated = true
        else truncated = true
        n = stream.read(buf)
    finally stream.close()
    val text = out.toString("UTF-8")
    if truncated then text + TruncationMarker else text

  def exec(
    command: String,
    args: List[String] = List.empty,
    workingDir: Option[String] = None,
    timeoutMs: Long = 30000
  )(using pp: ProcessPermission): ProcessResult =
    pp.validateCommand(command, args)
    val pb = ProcessBuilder((command :: args).asJava)
    workingDir.foreach(d => pb.directory(java.io.File(d)))
    val process = pb.start().nn
    try
      // Drain stdout and stderr on separate threads to avoid deadlock
      // when the process output fills the OS pipe buffer.
      @volatile var stdout = ""
      @volatile var stderr = ""
      val t1 = Thread(() => stdout = drainStream(process.getInputStream.nn))
      val t2 = Thread(() => stderr = drainStream(process.getErrorStream.nn))
      t1.setDaemon(true)
      t2.setDaemon(true)
      t1.start()
      t2.start()
      val finished = process.waitFor(timeoutMs, TimeUnit.MILLISECONDS)
      if !finished then
        process.destroyForcibly()
        t1.join(1000)
        t2.join(1000)
        throw RuntimeException(s"Process '$command' timed out after ${timeoutMs}ms")
      // The child exited, but a grandchild that inherited the pipes can keep
      // them open, which would block the drainers on read forever. Join with a
      // bounded timeout; if the drainers stall, kill the process (best-effort —
      // a surviving grandchild's pipe is out of our reach, but the drainers
      // are daemon threads and we return whatever was captured).
      t1.join(DrainJoinTimeoutMs)
      t2.join(DrainJoinTimeoutMs)
      if t1.isAlive || t2.isAlive then
        process.destroyForcibly()
        t1.join(1000)
        t2.join(1000)
      ProcessResult(process.exitValue(), stdout, stderr)
    catch
      case e: Exception =>
        process.destroyForcibly()
        throw e

  def execOutput(
    command: String,
    args: List[String] = List.empty
  )(using pp: ProcessPermission): String =
    exec(command, args).stdout
