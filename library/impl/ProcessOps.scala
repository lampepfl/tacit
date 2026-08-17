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

  /** Incrementally captures one process stream, byte-exact (trailing
   *  newlines preserved) and capped at [[MaxStreamBytes]]. The bytes live in
   *  a shared, synchronized buffer so the caller can read whatever arrived so
   *  far even if the drainer thread never finishes (a grandchild holding the
   *  pipe open). */
  private final class StreamCapture:
    private val out = java.io.ByteArrayOutputStream()
    @volatile private var truncated = false

    /** Drains `stream` to EOF on the current thread. Output beyond the cap is
     *  still read (so the process never blocks on a full pipe) but discarded. */
    def drain(stream: java.io.InputStream): Unit =
      val buf = new Array[Byte](8192)
      try
        var n = stream.read(buf)
        while n >= 0 do
          val room = MaxStreamBytes - out.size()
          if room > 0 then
            val keep = math.min(room, n)
            out.write(buf, 0, keep)
            if keep < n then truncated = true
          else truncated = true
          n = stream.read(buf)
      finally stream.close()

    /** Snapshot of the captured text (`ByteArrayOutputStream` is synchronized,
     *  so this is safe while a drain is still running). */
    def text: String =
      val captured = out.toString("UTF-8")
      if truncated then captured + TruncationMarker else captured

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
      val stdout = StreamCapture()
      val stderr = StreamCapture()
      val t1 = Thread(() => stdout.drain(process.getInputStream.nn))
      val t2 = Thread(() => stderr.drain(process.getErrorStream.nn))
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
      // The child has exited, but a grandchild that inherited the pipes can
      // keep them open, which would block the drainers on read forever. Join
      // with a bounded timeout and then return whatever was captured so far;
      // the stalled drainers are daemon threads and finish (or die with the
      // server) on their own. The surviving grandchild is not ours to kill:
      // the JVM only knows the direct child, which is already gone.
      t1.join(DrainJoinTimeoutMs)
      t2.join(DrainJoinTimeoutMs)
      ProcessResult(process.exitValue(), stdout.text, stderr.text)
    catch
      case e: Exception =>
        process.destroyForcibly()
        throw e

  def execOutput(
    command: String,
    args: List[String] = List.empty
  )(using pp: ProcessPermission): String =
    exec(command, args).stdout
