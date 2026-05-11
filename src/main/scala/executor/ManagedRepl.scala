package tacit
package executor

import core.Context
import Context.*

import dotty.tools.repl.*
import dotty.tools.dotc.reporting.Diagnostic

import java.io.{ByteArrayOutputStream, File => JFile, PrintStream}
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets

case class ExecutionResult(
    success: Boolean,
    output: String,
    error: Option[String] = None
)

object ManagedRepl:

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

  /** The library fat JAR provides the full classpath (Scala stdlib + library
    * classes + library dependencies), so we don't need `-usejavacp`.
    */
  private def replClasspathArgs(using Context): Array[String] =
    val classpath = JFile(ctx.config.libraryJarPath).getAbsolutePath

    val langaugeFeatures = List(
      "experimental.captureChecking",
      "experimental.modularity",
    ) ++ (if ctx.config.safeMode then List("experimental.safe") else Nil)

    Array(
      "-classpath", classpath,
      "-color:never",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Yexplicit-nulls",
      "-Ycheck-all-patmat",
      "-Wsafe-init",
      s"-language:${langaugeFeatures.mkString(",")}",
      s"-Xrepl-eval-log-dir:${ctx.config.recordPath.getOrElse("./log")}/eval",
      "-release:17",
      // "-Vprint:cc"
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

  /** Preamble injected before any user code so the capability API is in scope. */
  private[executor] def libraryPreamble(using Context): String =
    val jsonStr = ctx.config.libraryConfig.noSpaces
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
    s"""|import tacit.library.*
        |import dotty.tools.repl.eval.{Eval, EvalContext, EvalResult, evalLike, evalSafeLike}
        |import caps.*
        |object api extends InterfaceImpl("$jsonStr")
        |import api.*
        |""".stripMargin

  // We need a separate preamble for REPL, so the first repl object will be pure.
  private[executor] def libraryPreambleTracked(using Context): String =
    s"""|given IOCapability = GlobalIOCap
        |""".stripMargin

  /** We swap `System.out`/`System.err` around each execution to catch output the
   *  REPL driver doesn't route through `printStream` (notably compiler
   *  diagnostics under capture checking). Because those streams are
   *  process-global, concurrent executions would clobber each other's capture —
   *  this lock serializes them. It bounds session concurrency to 1; that's the
   *  cost of using the shared compiler output as our signal.
   */
  private val outputCaptureLock = Object()

  private def withOutputCapture(
    outputCapture: ByteArrayOutputStream,
    printStream: PrintStream
  )(run: => Unit): (String, Option[Exception]) =
    outputCaptureLock.synchronized:
      outputCapture.reset()
      val oldOut = System.out
      val oldErr = System.err
      System.setOut(printStream)
      System.setErr(printStream)
      val thrown =
        try
          run
          None
        catch case e: Exception => Option(e)
        finally
          System.setOut(oldOut)
          System.setErr(oldErr)
          printStream.flush()
      (outputCapture.toString(StandardCharsets.UTF_8).trim, thrown)
end ManagedRepl

/** Wraps a [[ReplDriver]] together with its output-capture streams and
  * accumulated [[State]]. Call [[init]] once, then [[run]] repeatedly; each
  * run accumulates into the persistent state.
  */
class ManagedRepl(using Context):
  import ManagedRepl.*

  private val outputCapture = new ByteArrayOutputStream()
  private val printStream = new PrintStream(outputCapture, true, StandardCharsets.UTF_8)
  private val driver = new OpenReplDriver(replClasspathArgs, printStream, Some(sandboxedClassLoader))
  private var state: State = driver.initialState

  /** Preamble compile errors are intentionally *not* captured — a broken
    * preamble is a programmer bug that should surface loudly.
    */
  def init(): this.type =
    // val (output, thrown) = withOutputCapture(outputCapture, printStream):
    state = driver.run(libraryPreamble)(using state)
    state = driver.run(libraryPreambleTracked)(using state)
    // For debugging preamble issues
    // println(output)
    // thrown.foreach(e => println(s"Preamble error: ${e.getMessage}"))
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
      ParseResult.complete(code)(using state) match
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

  /** For a [[Parsed]] we read compile-error state from its `StoreReporter`:
    * `errorCount` stays live even after the driver drains the diagnostic list
    * via `removeBufferedMessages`, so `hasErrors` is a structured signal
    * rather than a stdout heuristic. For allowlisted meta-commands (`:type`,
    * `:doc`, `:imports`) there is no reporter, so we treat dispatch as
    * successful and let any error text surface in the captured output.
    */
  private def dispatch(res: ParseResult): ExecutionResult =
    val (output, thrown) = withOutputCapture(outputCapture, printStream):
      state = driver.runParseResult(res)(using state)
    thrown match
      case Some(e) =>
        ExecutionResult(false, output, Option(e.getMessage))
      case None =>
        val compileFailed = res match
          case p: Parsed => p.reporter.hasErrors
          case _         => false
        ExecutionResult(!compileFailed, output)

  private def formatDiagnostics(diags: List[Diagnostic]): String =
    diags.map: d =>
      val pos = d.pos
      if pos != null && pos.exists then s"Line ${pos.line + 1}: ${d.message}"
      else d.message
    .mkString("\n")
