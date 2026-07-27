package tacit.library

import language.experimental.captureChecking
import caps.*

import java.io.{File => JFile, PrintStream, FileOutputStream}
import java.nio.file.{Files, Path, Paths}

@assumeSafe
abstract class InterfaceImpl(
  configJson: String
) extends Interface:

  private val config = LibraryConfig.fromJson(configJson)

  private val DefaultClassifiedPatterns: Set[String] = Set(
    ".ssh", ".gnupg", ".env", ".env.*", ".netrc", ".npmrc", ".pypirc",
    ".docker", ".kube", ".aws", ".azure", ".gcloud",
  )
  private val strictMode: Boolean = config.strictMode.getOrElse(true)
  private val classifiedPatterns: Set[String] = config.classifiedPaths.getOrElse(DefaultClassifiedPatterns)
  /** Outer bound on file-system roots: every `requestFileSystem(root)` must
   *  resolve to a path within one of these. When unset, it defaults to the
   *  server's current working directory, so the sandbox is confined to that
   *  subtree by default (fail closed) rather than allowing any root. Set it
   *  explicitly to widen or relocate the bound. */
  private val allowedRoots: Set[String] =
    config.allowedRoots.getOrElse(Set(InterfaceImpl.currentWorkingDir))
  private val commandPermissions: Option[Set[String]] = config.commandPermissions
  private val networkPermissions: Option[Set[String]] = config.networkPermissions
  private val llmConfig: Option[LlmConfig] = config.llm
  /** Whether `writeClassified` is permitted. Defaults to true (current
   *  behavior); set to false to protect the *integrity* of classified files
   *  (e.g. `.ssh/authorized_keys`) against agent writes — confidentiality is
   *  already enforced by the classified-read path. */
  private val classifiedWriteEnabled: Boolean = config.classifiedWrite.getOrElse(true)

  /** Optional secondary sink that receives the *unmasked* form of printed values.
   *  When configured, `println`/`print`/`printf` still write a masked view
   *  (`Classified(***)`) to the normal output, but also append the fully
   *  unwrapped content to this file — only the end user reading that file
   *  can see classified data. */
  private val secureWriter: Option[PrintStream] = config.secureOutput.map(InterfaceImpl.secureWriterFor)

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
    case other            => other

  // create real FileSystem by default, but allow tests to override with a virtual one
  protected def createFS(root: String, filter: String -> Boolean, classifiedPatterns: Set[String]): FileSystem =
    new RealFileSystem(root, filter, classifiedPatterns, classifiedWriteEnabled)

  export FileOps.*
  export ProcessOps.*
  export WebOps.*

  private val llmOps = LlmOps(llmConfig)

  export llmOps.*

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

  /** Resolves a path to the same canonical form [[RealFileSystem]] uses for its
   *  root: absolute + normalized, then through symlinks when the path exists.
   *  Resolving symlinks matters for the bound check below. Otherwise a symlink
   *  *named* inside an allowed root but *pointing* outside it would pass. */
  private def resolveRootForBound(p: String): Path =
    val abs = Paths.get(p).toAbsolutePath.nn.normalize.nn
    if Files.exists(abs) then abs.toRealPath().nn else abs

  /** Entry-time outer-bound check for [[requestFileSystem]]: the requested root
   *  must resolve to a path equal to, or nested under, one of `allowedRoots`
   *  (which defaults to the current working directory). */
  private def requireRootAllowed(root: String): Unit =
    val resolved = resolveRootForBound(root)
    val permitted = allowedRoots.exists(allowed => resolved.startsWith(resolveRootForBound(allowed)))
    if !permitted then
      throw SecurityException(
        s"Access denied: filesystem root '$root' is not within any allowed root $allowedRoots"
      )

  def requestFileSystem[T](root: String)(op: FileSystem^ ?=> T)(using IOCapability): T =
    requireRootAllowed(root)
    val fs = createFS(root, _ => true, classifiedPatterns)
    op(using fs)

  /** Entry-time subset check shared by [[requestExecPermission]] and
   *  [[requestNetwork]]: each item in `scope` must match at least one pattern
   *  in `policy`. For command patterns that carry args (e.g. `"sbt run *"`),
   *  we match against the pattern's command-word (the part before the first
   *  space) so a bare scope command like `"sbt"` passes entry — per-invocation
   *  arg filtering still happens at runtime. */
  private def requireSubset(
    scope: Set[String],
    policy: Set[String],
    kind: String
  ): Unit =
    scope.foreach: item =>
      val matched = policy.exists: pattern =>
        val head = pattern.takeWhile(_ != ' ')
        GlobMatcher.matches(item, head)
      if !matched then
        throw SecurityException(
          s"Access denied: scope $kind '$item' is not permitted by server policy $policy"
        )

  def requestExecPermission[T](commands: Set[String])(op: ProcessPermission^ ?=> T)(using IOCapability): T =
    // Server-configured commandPermissions is the outer bound: every command
    // the scope declares must be permitted by some pattern's command-word.
    commandPermissions.foreach(p => requireSubset(commands, p, "command"))
    val perm = new ProcessPermissionImpl(commands, strictMode, commandPermissions)
    op(using perm)

  def requestNetwork[T](hosts: Set[String])(op: Network^ ?=> T)(using IOCapability): T =
    // Server-configured networkPermissions is the outer bound: every host the
    // scope declares must match at least one pattern.
    networkPermissions.foreach(p => requireSubset(hosts, p, "host"))
    val net = new NetworkImpl(hosts)
    op(using net)

  def classify[T](value: T): Classified[T] = ClassifiedImpl.wrap(value)

  def access(path: String)(using fs: FileSystem): FileEntry^{fs} =
    fs.access(path)

  def readClassified(path: String)(using fs: FileSystem): Classified[String] =
    fs.access(path).readClassified()

  def writeClassified(path: String, content: Classified[String])(using fs: FileSystem): Unit =
    if !classifiedWriteEnabled then
      throw SecurityException(
        s"Access denied: writeClassified is disabled by the server configuration (classifiedWrite = false)"
      )
    fs.access(path).writeClassified(content)

object InterfaceImpl:
  /** The server process's current working directory, used as the default
    * `allowedRoots` bound. Falls back to "." if the `user.dir` property is
    * absent (it normally is not). */
  private[library] def currentWorkingDir: String =
    Option(System.getProperty("user.dir")).getOrElse(".")

  /** One append-mode `PrintStream` per secureOutput path, shared process-wide.
    * A fresh `InterfaceImpl` is built on every REPL init (and stateless
    * `execute` builds a REPL per call), so opening a new `FileOutputStream` in
    * each instance would leak a file descriptor per execution until the process
    * runs out. Cache and reuse keyed by canonical path, so different spellings
    * of the same file (`a/./log`, `a/sub/../log`) share one stream. */
  private val secureWriters = scala.collection.mutable.HashMap[String, PrintStream]()

  private[library] def secureWriterFor(path: String): PrintStream =
    secureWriters.synchronized:
      val file = JFile(path).getAbsoluteFile.nn
      val key =
        try file.getCanonicalPath.nn
        catch case _: java.io.IOException => file.getAbsolutePath.nn
      secureWriters.getOrElseUpdate(key, {
        Option(file.getParentFile).foreach(_.mkdirs())
        val isNew = !file.exists()
        val stream = PrintStream(FileOutputStream(file, true), true, "UTF-8")
        // The sink receives unmasked classified content, so restrict a newly
        // created file to the owner (rw-------). Best-effort: POSIX-only.
        if isNew then
          try
            Files.setPosixFilePermissions(
              file.toPath,
              java.nio.file.attribute.PosixFilePermissions.fromString("rw-------")
            )
          catch case _: UnsupportedOperationException | _: java.io.IOException => ()
        stream
      })