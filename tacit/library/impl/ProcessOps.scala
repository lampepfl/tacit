package tacit.library

import language.experimental.captureChecking

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters.*

object ProcessOps:
  /** Drains an input stream into a string on the current thread. */
  private def drainStream(stream: java.io.InputStream): String =
    val source = scala.io.Source.fromInputStream(stream)
    try source.getLines().mkString("\n")
    finally source.close()

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
      val t1 = Thread(() => stdout = drainStream(process.getInputStream))
      val t2 = Thread(() => stderr = drainStream(process.getErrorStream))
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
      t1.join()
      t2.join()
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
