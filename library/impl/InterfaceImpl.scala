package tacit.library

import language.experimental.captureChecking
import caps.*

import java.nio.file.Path

@assumeSafe
abstract class InterfaceImpl(
  private val config: LibraryConfig = LibraryConfig()
) extends Interface:

  private val strictMode: Boolean = config.strictMode.getOrElse(true)
  private val classifiedPaths: Set[Path] = config.classifiedPaths.getOrElse(Set.empty)
    .map(Path.of(_).toAbsolutePath.normalize)
  private val llmConfig: Option[LlmConfig] = config.llm

  protected def createFS(root: String, filter: String -> Boolean, classifiedPaths: Set[Path]): FileSystem

  export FileOps.*
  export ProcessOps.*
  export WebOps.*

  // IOCapability's private constructor means user code cannot create one.
  // The null sentinel is safe: IOCapability is only used as a type-level
  // capability witness, never dereferenced at runtime.
  val iocap: IOCapability = null.asInstanceOf[IOCapability]
  
  private val llmOps = new LlmOps(llmConfig)
  export llmOps.chat

  def println(x: Any)(using IOCapability): Unit = scala.Predef.println(x)
  
  def println()(using IOCapability): Unit = scala.Predef.println()

  def print(x: Any)(using IOCapability): Unit = scala.Predef.print(x)

  def printf(fmt: String, args: Any*)(using IOCapability): Unit = scala.Predef.printf(fmt, args*)

  def requestFileSystem[T](root: String)(op: FileSystem^ ?=> T)(using IOCapability): T =
    val rootPath = Path.of(root).toAbsolutePath.normalize
    val relevantClassified = classifiedPaths
      .map(_.toAbsolutePath.normalize)
      .filter(cp => cp.startsWith(rootPath) || rootPath.startsWith(cp))
    val fs = createFS(root, _ => true, relevantClassified)
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