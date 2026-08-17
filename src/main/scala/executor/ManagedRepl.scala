package tacit
package executor

import core.Context
import Context.*

import dotty.tools.repl.*
import dotty.tools.dotc.reporting.Diagnostic

import java.io.{File => JFile, PrintStream}
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets
import java.util.jar.JarFile

case class ExecutionResult(
    success: Boolean,
    output: String,
    error: Option[String] = None
)

object ManagedRepl:

  /** Hard cap on captured execution output (bytes of UTF-8). Without it,
    * `while(true) println("x")` grows the capture buffer until the server
    * runs out of memory. Output beyond the cap is discarded. */
  val MaxOutputBytes: Int = 10 * 1024 * 1024

  /** Appended to captured output that hit [[MaxOutputBytes]], so the client
    * can tell the output was cut rather than complete. */
  val TruncationMarker: String = "\n... [output truncated: exceeded 10 MiB capture limit]"

  /** An [[java.io.OutputStream]] that retains at most `limit` bytes and
    * silently discards the rest, setting [[truncated]] once anything is
    * dropped. Writes never throw, so the cap logic can never break the
    * evaluation path (PrintStream also swallows I/O errors on its own). */
  private[executor] final class BoundedOutputStream(private val limit: Int) extends java.io.OutputStream:
    private val buf = new java.io.ByteArrayOutputStream(math.min(limit, 8192))
    @volatile var truncated: Boolean = false

    override def write(b: Int): Unit =
      if buf.size() < limit then buf.write(b)
      else truncated = true

    override def write(b: Array[Byte], off: Int, len: Int): Unit =
      val room = limit - buf.size()
      if room <= 0 then truncated = true
      else
        if len > room then truncated = true
        buf.write(b, off, math.min(len, room))

    def resetCapture(): Unit =
      buf.reset()
      truncated = false

    def capturedString: String = buf.toString(java.nio.charset.StandardCharsets.UTF_8)
  end BoundedOutputStream

  /** Subclass of [[ReplDriver]] that exposes the protected `runBody`/`interpret`
    * pair, so we can feed a pre-parsed [[ParseResult]] to the driver instead of
    * re-parsing the same source inside `driver.run(String)`.
    */
  private class OpenReplDriver(
      settings: Array[String],
      out: PrintStream,
      cl: Option[ClassLoader]
  ) extends ReplDriver(settings, out, cl):
    def runParseResult(res: ParseResult)(using State): State =
      runBody(interpret(res))

  /** Reads a UTF-8 text resource bundled inside the library JAR (e.g. the
    * `Interface.scala` API reference shown by `show_interface`). The reference
    * lives with the library it documents rather than on the server's own
    * classpath. Returns `None` if the JAR or entry can't be read.
    */
  def readLibraryResource(name: String)(using Context): Option[String] =
    try
      val jar = JarFile(ctx.config.libraryJarPath)
      try
        Option(jar.getJarEntry(name)).map: entry =>
          val stream = jar.getInputStream(entry)
          try scala.io.Source.fromInputStream(stream)(using scala.io.Codec.UTF8).mkString
          finally stream.close()
      finally jar.close()
    catch case _: Exception => None

  /** The library fat JAR provides the full classpath (Scala stdlib + library
    * classes + library dependencies), so we don't need `-usejavacp`.
    */
  private def replClasspathArgs(using Context): Array[String] =
    val classpath = JFile(ctx.config.libraryJarPath).getAbsolutePath
    Array(
      "-classpath", classpath,
      "-color:never",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Yexplicit-nulls",
      "-Ycheck-all-patmat",
      "-Wsafe-init",
      "-language:experimental.captureChecking",
      "-language:experimental.modularity",
      // Only REPL-defined classes get the interrupt instrumentation. With the
      // default (`true`) the REPL loader re-defines every non-JDK class it
      // touches, including the library's, so the library would exist twice
      // (once in `sandboxedClassLoader`, once instrumented in the REPL loader)
      // with separate static state, and the config registered by
      // `configureLibrary` would not be the one agent code sees.
      "-Xrepl-interrupt-instrumentation:local"
    )

  /** Exposes only JDK platform classes and the library JAR, keeping user code
    * away from server internals (tacit.core, tacit.mcp, tacit.executor) and
    * server dependencies (circe, scopt, etc.).
    */
  private def sandboxedClassLoader(using Context): ClassLoader =
    val libraryUrl = JFile(ctx.config.libraryJarPath).toURI.toURL
    new URLClassLoader(
      Array(libraryUrl),
      ClassLoader.getPlatformClassLoader
    )

  /** Preamble injected before any user code so the capability API is in
    * scope. `SandboxInterface` has no constructor parameters: its policy is
    * the config the server registered in [[ManagedRepl.configureLibrary]].
    */
  private[executor] val libraryPreamble: String =
    """|import tacit.library.*
       |import caps.*
       |@assumeSafe object api extends SandboxInterface
       |import api.*
       |@assumeSafe given IOCapability = GlobalIOCap
       |""".stripMargin

  /** Register the sandbox's library config *before* any REPL code runs, by
    * calling `InterfaceImpl.configure` on the library classes loaded through
    * the REPL's own sandboxed class loader (static state is per loader). The
    * method is not accessible from REPL code, hence reflection. Doing this
    * from the server rather than from the preamble also sidesteps REPL
    * evaluation order: REPL objects initialize lazily, so nothing that
    * depends on `api` having been touched first would be safe.
    */
  private def configureLibrary(loader: ClassLoader, libraryConfigJson: String): Unit =
    val module = Class.forName("tacit.library.InterfaceImpl$", true, loader)
    val instance = module.getField("MODULE$").get(null)
    try module.getMethod("configure", classOf[String]).invoke(instance, libraryConfigJson)
    catch case e: java.lang.reflect.InvocationTargetException =>
      throw Option(e.getCause).getOrElse(e)

  /** We swap `System.out`/`System.err` around each execution to catch output the
   *  REPL driver doesn't route through `printStream` (notably compiler
   *  diagnostics under capture checking). Because those streams are
   *  process-global, concurrent executions would clobber each other's capture —
   *  this lock serializes them. It bounds session concurrency to 1; that's the
   *  cost of using the shared compiler output as our signal.
   */
  private val outputCaptureLock = Object()

  private def withOutputCapture(
    outputCapture: BoundedOutputStream,
    printStream: PrintStream
  )(run: => Unit): (String, Option[Throwable]) =
    outputCaptureLock.synchronized:
      outputCapture.resetCapture()
      val oldOut = System.out
      val oldErr = System.err
      System.setOut(printStream)
      System.setErr(printStream)
      val thrown =
        try
          run
          None
        // NonFatal (not just Exception) so a non-fatal interpreter *Error* — e.g.
        // dotty's `AssertionError: denotation ... invalid in run N` on certain
        // symbols is reported as a failed execution instead of escaping to the
        // JSON-RPC loop, where it would leave the client without any response.
        catch case scala.util.control.NonFatal(e) => Option(e)
        finally
          System.setOut(oldOut)
          System.setErr(oldErr)
          printStream.flush()
      val captured = outputCapture.capturedString.trim
      val output = if outputCapture.truncated then captured + TruncationMarker else captured
      (output, thrown)
end ManagedRepl

/** Wraps a [[ReplDriver]] together with its output-capture streams and
  * accumulated [[State]]. Call [[init]] once, then [[run]] repeatedly; each
  * run accumulates into the persistent state.
  */
class ManagedRepl(using Context):
  import ManagedRepl.*

  private val outputCapture = new BoundedOutputStream(MaxOutputBytes)
  private val printStream = new PrintStream(outputCapture, true, StandardCharsets.UTF_8)
  private val sandboxLoader = sandboxedClassLoader
  private val driver = new OpenReplDriver(replClasspathArgs, printStream, Some(sandboxLoader))
  private var state: State = driver.initialState

  /** Preamble compile errors are intentionally *not* captured: a broken
    * preamble is a programmer bug that should surface loudly.
    */
  def init(): this.type =
    configureLibrary(sandboxLoader, ctx.config.libraryConfig.noSpaces)
    state = driver.run(libraryPreamble)(using state)
    if ctx.config.safeMode then
      state = driver.run("import language.experimental.safe")(using state)
    this

  /** Execute `code` against the current state.
    *
    * Reimplements the dispatch of `ReplDriver.run(String)` so we can reject
    * REPL meta-commands outside the `:type`/`:doc`/`:imports` allowlist
    * (blocking `:load`, `:sh`, `:quit`, ...), parse exactly once, and read
    * compile-error state structurally instead of scraping stdout.
    */
  def run(code: String): ExecutionResult =
    val violations = CodeValidator.validate(code)
    if violations.nonEmpty then
      ExecutionResult(false, "", Some(CodeValidator.formatErrors(violations)))
    else
      ParseResult(terminated(code))(using state) match
        case p: Parsed =>
          dispatch(p)
        case cmd @ (_: TypeOf | _: DocOf | Imports) =>
          dispatch(cmd)
        case _: Command =>
          ExecutionResult(false, "",
            Some("Only :type, :doc, and :imports REPL commands are allowed."))
        case Newline =>
          ExecutionResult(true, "")
        case SyntaxErrors(_, errors, _) =>
          ExecutionResult(false, "",
            Some("Syntax error:\n" + formatDiagnostics(errors)))
        case other =>
          ExecutionResult(false, "", Some(s"Unexpected parse result: $other"))

  /** `code` with trailing whitespace replaced by a single newline. The REPL
    * parser treats input that ends inside an indentation region without a
    * newline (`x match` followed by `case` lines, `def f =` with an indented
    * body, ...) as *incomplete* and reports "unindent expected, but eof
    * found"; that is how the interactive REPL knows to keep reading. Our
    * clients send complete snippets, so we close them ourselves. Only the
    * tail is touched, so line numbers in diagnostics are unaffected.
    */
  private def terminated(code: String): String =
    code.stripTrailing() + "\n"

  /** Dispatch a parse result, honoring the optional execution timeout. */
  private def dispatch(res: ParseResult): ExecutionResult =
    ctx.config.executionTimeoutMs match
      case None        => adopt(res, evaluate(res))
      case Some(limit) => dispatchWithTimeout(res, limit)

  /** Runs `res` against the current `state` and returns the resulting state
    * alongside captured output. Deliberately does *not* mutate `this.state`, so
    * the caller decides whether to adopt the new state, so a timed-out run can
    * be discarded without corrupting the session.
    */
  private def evaluate(res: ParseResult): (State, String, Option[Throwable]) =
    var newState = state
    val (output, thrown) = withOutputCapture(outputCapture, printStream):
      newState = driver.runParseResult(res)(using state)
    (newState, output, thrown)

  /** Adopt an evaluation result as the new session state and turn it into an
    * [[ExecutionResult]].
    *
    * For a [[Parsed]] we read compile-error state from its `StoreReporter`:
    * `errorCount` stays live even after the driver drains the diagnostic list
    * via `removeBufferedMessages`, so `hasErrors` is a structured signal
    * rather than a stdout heuristic. For allowlisted meta-commands (`:type`,
    * `:doc`, `:imports`) there is no reporter, so we treat dispatch as
    * successful and let any error text surface in the captured output.
    */
  private def adopt(res: ParseResult, evaluated: (State, String, Option[Throwable])): ExecutionResult =
    val (newState, output, thrown) = evaluated
    state = newState
    thrown match
      case Some(e) =>
        ExecutionResult(false, output, Option(e.getMessage))
      case None =>
        val compileFailed = res match
          case p: Parsed => p.reporter.hasErrors
          case _         => false
        ExecutionResult(!compileFailed, output)

  /** Run the evaluation on a worker thread, bounding it to `limitMs`.
    *
    * On timeout the client gets a prompt error instead of hanging, and the
    * session keeps its prior state (the abandoned statement has no observable
    * effect). The interrupt is best-effort: the REPL instruments loops in
    * REPL-defined classes with interruption checks
    * (`-Xrepl-interrupt-instrumentation:local`), and blocking I/O and sleeps
    * respond to it, but a CPU-bound loop inside library or JDK code keeps
    * running in the background and continues to hold the process-global
    * output lock. This is *not* a hard sandbox; true preemption requires
    * process isolation.
    */
  private def dispatchWithTimeout(res: ParseResult, limitMs: Long): ExecutionResult =
    val resultRef = java.util.concurrent.atomic.AtomicReference[(State, String, Option[Throwable])]()
    val worker = Thread(() => resultRef.set(evaluate(res)))
    worker.setName("tacit-repl-eval")
    worker.setDaemon(true)
    worker.start()
    worker.join(limitMs)
    if worker.isAlive then
      worker.interrupt()
      ExecutionResult(false, "", Some(s"Execution timed out after ${limitMs}ms"))
    else
      resultRef.get() match
        case null      => ExecutionResult(false, "", Some("Execution failed (no result; possible fatal error)"))
        case evaluated => adopt(res, evaluated)

  private def formatDiagnostics(diags: List[Diagnostic]): String =
    diags.map: d =>
      val pos = d.pos
      if pos != null && pos.exists then s"Line ${pos.line + 1}: ${d.message}"
      else d.message
    .mkString("\n")
