package tacit
package executor

import core.Context
import Context.*

import dotty.tools.repl.{ReplDriver, State}

import scala.collection.concurrent.TrieMap

import java.io.{ByteArrayOutputStream, File => JFile, PrintStream}
import java.net.URLClassLoader
import java.nio.charset.StandardCharsets
import java.util.UUID

/** Result of code execution */
case class ExecutionResult(
    success: Boolean,
    output: String,
    error: Option[String] = None
)

/** Executes Scala code snippets */
object ScalaExecutor:

  /** Returns the REPL compiler args. The library fat JAR provides the full classpath
    * (Scala stdlib + library classes + library dependencies).
    */
  private[executor] def replClasspathArgs(using Context): Array[String] =
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
      "-language:experimental.modularity"
    )

  /** Creates a sandboxed classloader that only exposes JDK platform classes
    * and the library JAR. This prevents user code from accessing server internals
    * (tacit.core, tacit.mcp, tacit.executor) or server dependencies (circe, scopt, etc.).
    */
  private[executor] def sandboxedClassLoader(using Context): ClassLoader =
    val libraryUrl = JFile(ctx.config.libraryJarPath).toURI.toURL
    new URLClassLoader(
      Array(libraryUrl),
      ClassLoader.getPlatformClassLoader  // JDK platform classes only, no application classes
    )

  /** Preamble code injected before user code to make the library API available. */
  private[executor] def libraryPreamble(using Context): String =
    val jsonStr = ctx.config.libraryConfig.noSpaces
      .replace("\\", "\\\\")
      .replace("\"", "\\\"")
    s"""|import tacit.library.*
        |import caps.*
        |@assumeSafe val api: Interface^ = new InterfaceImpl(LibraryConfig.fromJson("$jsonStr")) {
        |  def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
        |    new RealFileSystem(java.nio.file.Path.of(root), filter, classifiedPatterns)
        |}
        |import api.*
        |@assumeSafe given IOCapability = iocap
        |""".stripMargin

  /** Wraps user code in a `def run() = ...; run()` block to avoid capture checking REPL errors. */
  private[executor] def wrapCode(code: String, wrap: Boolean): String =
    if !wrap then code
    else
      val indented = code.linesIterator.map(line => s"  $line").mkString("\n")
      s"def run()(using IOCapability): Any =\n$indented\nrun()"

  /** Returns Some(errorResult) on validation failure, None on success. */
  private[executor] def validated(code: String): Option[ExecutionResult] =
    CodeValidator.validate(code).left.toOption.map(violations =>
      ExecutionResult(false, "", Some(CodeValidator.formatErrors(violations)))
    )

  /** Global lock for System.out/err redirection. The REPL compiler writes to
    * System.out/err directly, so only one execution can capture at a time.
    */
  private val outputCaptureLock = new Object

  /** Redirects stdout/stderr to the given print stream, runs the block, and captures output.
    * Detects Scala 3 compilation errors in the output (lines starting with `-- [E`)
    * and sets success=false accordingly.
    */
  private[executor] def withOutputCapture(
    outputCapture: ByteArrayOutputStream,
    printStream: PrintStream
  )(run: => Unit): ExecutionResult =
    outputCaptureLock.synchronized:
      outputCapture.reset()
      try
        val oldOut = System.out
        val oldErr = System.err
        System.setOut(printStream)
        System.setErr(printStream)
        try run
        finally
          System.setOut(oldOut)
          System.setErr(oldErr)
        printStream.flush()
        val output = outputCapture.toString(StandardCharsets.UTF_8).trim
        val hasCompileErrors = output.linesIterator.exists(_.startsWith("-- [E"))
        ExecutionResult(!hasCompileErrors, output)
      catch
        case e: Exception =>
          printStream.flush()
          ExecutionResult(false, outputCapture.toString(StandardCharsets.UTF_8).trim,
            Some(s"${e.getClass.getSimpleName}: ${e.getMessage}"))

  /** Execute a Scala code snippet stateless and return the result */
  def execute(code: String)(using Context): ExecutionResult =
    validated(code).getOrElse:
      val outputCapture = new ByteArrayOutputStream()
      val printStream = new PrintStream(outputCapture, true, StandardCharsets.UTF_8)
      val driver = new ReplDriver(replClasspathArgs, printStream, Some(sandboxedClassLoader))
      // If library preamble fails to compile, move this line inside withOutputCapture 
      // to capture the error output.
      var state = driver.run(libraryPreamble)(using driver.initialState)  
      // state = driver.run("import language.experimental.safe")(using state)
      withOutputCapture(outputCapture, printStream):
        state = driver.run(wrapCode(code, ctx.config.wrappedCode))(using state)
end ScalaExecutor

/** A REPL session that maintains state across executions */
class ReplSession(val id: String)(using Context):
  import ScalaExecutor.*

  private val outputCapture = new ByteArrayOutputStream()
  private val printStream = new PrintStream(outputCapture, true, StandardCharsets.UTF_8)

  private val driver = new ReplDriver(
    replClasspathArgs,
    printStream,
    Some(sandboxedClassLoader)
  )
  private var state: State =
    val s0 = driver.initialState
    // Run preamble once to make library API available in the session
    driver.run(libraryPreamble)(using s0)

  /** Execute code in this session and return the result */
  def execute(code: String): ExecutionResult =
    validated(code).getOrElse:
      withOutputCapture(outputCapture, printStream):
        state = driver.run(code)(using state)

object ReplSession:
  def create(using Context): ReplSession = new ReplSession(UUID.randomUUID().toString)

/** Manages multiple REPL sessions. Thread-safe via TrieMap. */
class SessionManager(using Context):
  private val sessions = TrieMap[String, ReplSession]()

  /** Create a new session and return its ID */
  def createSession(): String =
    val session = ReplSession.create
    sessions(session.id) = session
    session.id

  /** Delete a session by ID */
  def deleteSession(sessionId: String): Boolean =
    sessions.remove(sessionId).isDefined

  /** Get a session by ID */
  def getSession(sessionId: String): Option[ReplSession] =
    sessions.get(sessionId)

  /** Execute code in a specific session */
  def executeInSession(sessionId: String, code: String): Either[String, ExecutionResult] =
    sessions.get(sessionId) match
      case Some(session) => Right(session.execute(code))
      case None => Left(s"Session not found: $sessionId")

  /** List all active session IDs */
  def listSessions(): List[String] =
    sessions.keys.toList
