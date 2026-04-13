package tacit.library

import language.experimental.captureChecking
import caps.*

@assumeSafe
abstract class InterfaceImpl(
  private val config: LibraryConfig = LibraryConfig()
) extends Interface:

  private val DefaultClassifiedPatterns: Set[String] = Set(
    ".ssh",
    ".gnupg",
    ".env",
    ".env.*",
    ".netrc",
    ".npmrc",
    ".pypirc",
    ".docker",
    ".kube",
    ".aws",
    ".azure",
    ".gcloud",
  )
  private val strictMode: Boolean = config.strictMode.getOrElse(true)
  private val classifiedPatterns: Set[String] = config.classifiedPaths.getOrElse(DefaultClassifiedPatterns)
  private val llmConfig: Option[LlmConfig] = config.llm

  /** Optional secondary sink that receives the *unmasked* form of printed values.
   *  When configured, `println`/`print`/`printf` still write a masked view
   *  (`Classified(***)`) to the normal output, but also append the fully
   *  unwrapped content to this file — only the end user reading that file
   *  can see classified data. */
  private val secureWriter: Option[java.io.PrintStream] = config.secureOutput.map { path =>
    val file = new java.io.File(path)
    Option(file.getAbsoluteFile.nn.getParentFile).foreach(_.mkdirs())
    new java.io.PrintStream(new java.io.FileOutputStream(file, true), true, "UTF-8")
  }

  private def withSecureOut(op: => Unit): Unit =
    secureWriter.foreach(w => scala.Console.withOut(w)(op))

  private def unwrapForSecure(x: Any): Any = x match
    case c: Classified[?] =>
      ClassifiedImpl.unwrap(c).fold(
        e => s"<classified error: ${e.getMessage}>",
        v => v
      )
    case other => other

  private def maskForMain(x: Any): Any = x match
    case _: Classified[?] => "Classified(***)"
    case other => other

  protected def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem

  export FileOps.*
  export ProcessOps.*
  export WebOps.*

  // IOCapability's private constructor means user code cannot create one.
  // The null sentinel is safe: IOCapability is only used as a type-level
  // capability witness, never dereferenced at runtime.
  val iocap: IOCapability = null.asInstanceOf[IOCapability]
  
  private val llmOps = new LlmOps(llmConfig)
  export llmOps.chat

  def println(x: Any)(using IOCapability): Unit =
    // Classified.toString already returns "Classified(***)" so the
    // main stream is automatically masked.
    scala.Predef.println(x)
    withSecureOut(scala.Predef.println(unwrapForSecure(x)))

  def println()(using IOCapability): Unit =
    scala.Predef.println()
    withSecureOut(scala.Predef.println())

  def print(x: Any)(using IOCapability): Unit =
    scala.Predef.print(x)
    withSecureOut(scala.Predef.print(unwrapForSecure(x)))

  def printf(fmt: String, args: Any*)(using IOCapability): Unit =
    // printf's format specifiers bypass toString, so mask Classified args
    // explicitly for the main stream.
    scala.Predef.printf(fmt, args.map(maskForMain)*)
    withSecureOut(scala.Predef.printf(fmt, args.map(unwrapForSecure)*))

  def requestFileSystem[T](root: String)(op: FileSystem^ ?=> T)(using IOCapability): T =
    val fs = createFS(root, _ => true, classifiedPatterns)
    op(using fs)

  def requestExecPermission[T](commands: Set[String])(op: ProcessPermission^ ?=> T)(using IOCapability): T =
    val perm = new ProcessPermission(commands, strictMode)
    op(using perm)

  def requestNetwork[T](hosts: Set[String])(op: Network^ ?=> T)(using IOCapability): T =
    val net = new Network(hosts)
    op(using net)

  def classify[T](value: T): Classified[T] = ClassifiedImpl.wrap(value)

  def access(path: String)(using fs: FileSystem): FileEntry^{fs} =
    fs.access(path)

  def readClassified(path: String)(using fs: FileSystem): Classified[String] =
    fs.access(path).readClassified()

  def writeClassified(path: String, content: Classified[String])(using fs: FileSystem): Unit =
    fs.access(path).writeClassified(content)