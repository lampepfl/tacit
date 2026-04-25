package tacit.executor

import java.io.File
import java.nio.file.Files
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.time.ZoneOffset
import java.util.concurrent.atomic.AtomicLong

/** Records user-submitted code and execution results to log files.
  *
  * Each call to `record` writes two files under `dir`:
  *   - `<timestamp>_<seq>_<session>.scala` — the submitted code
  *   - `<timestamp>_<seq>_<session>.result` — the execution result
  *
  * The sequence counter ensures unique filenames even when
  * multiple executions share the same millisecond timestamp.
  */
class CodeRecorder(dir: File):
  dir.mkdirs()

  private val dirPath = dir.toPath
  private val tsFormat = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSS").withZone(ZoneOffset.UTC)
  private val counter = AtomicLong(0)

  def record(code: String, sessionId: String, result: ExecutionResult): Unit =
    val base = s"${tsFormat.format(Instant.now())}_%04d_$sessionId".format(counter.getAndIncrement())
    Files.writeString(dirPath.resolve(s"$base.scala"), code)
    val status = if result.success then "success" else "failure"
    val body = StringBuilder(s"status: $status\n${result.output}")
    result.error.foreach(e => body.append(s"\nError: $e"))
    Files.writeString(dirPath.resolve(s"$base.result"), body.toString)

  def close(): Unit = ()
