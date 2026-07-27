package tacit.library

import language.experimental.captureChecking

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.{Success, Failure}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, FileVisitResult, Path, Paths, SimpleFileVisitor}
import java.nio.file.attribute.BasicFileAttributes

class RealFileSystem private[library] (
  val root: String,
  check: String -> Boolean = _ => true,
  protected val classifiedPatterns: Set[String] = Set.empty,
  classifiedWrite: Boolean = true
) extends BaseFileSystem:
  protected override val classifiedWriteEnabled: Boolean = classifiedWrite
  protected val normalizedRoot: Path =
    val abs = Paths.get(root).toAbsolutePath.normalize
    if Files.exists(abs) then abs.toRealPath() else abs
  protected def pathCheck(relativePath: String): Boolean = check(relativePath)

  /** Resolves symlinks in a path. For existing paths, uses toRealPath().
    * For non-existing paths, resolves the nearest existing ancestor and
    * appends the remaining segments, so that symlinks in parent directories
    * are still resolved (e.g. /tmp -> /private/tmp on macOS).
    */
  private def resolveReal(absPath: Path): Path =
    if Files.exists(absPath) then absPath.toRealPath()
    else
      val parent = absPath.getParent
      val fileName = absPath.getFileName
      if parent != null && fileName != null && parent != absPath then
        resolveReal(parent).resolve(fileName)
      else absPath

  /** Resolves symlinks in an absolute, normalized path and validates that the
    * result is within the allowed root. Follows symlinks to prevent
    * symlink-based escape attacks.
    */
  private def resolveAndCheck(absPath: Path): Path =
    val resolved = resolveReal(absPath)
    if !resolved.startsWith(normalizedRoot) then
      throw SecurityException(
        s"Access denied: $resolved is outside root $normalizedRoot"
      )
    resolved

  /** Resolves and validates that a path is within the allowed root. */
  private def resolvePath(target: String): Path =
    resolveAndCheck(Paths.get(target).toAbsolutePath.normalize)

  private class FileEntryImpl(jpath: Path) extends FileEntry(this):
    /** Re-resolves and re-validates the path immediately before use.
      *
      * TOCTOU mitigation: `access()` resolves symlinks once, but a parent
      * symlink swapped between that resolution and a later operation would
      * redirect the operation outside the root. Re-running the resolution and
      * containment check here narrows the window — it cannot fully close the
      * race (a swap between this check and the actual filesystem syscall is
      * still possible; `java.nio.file` offers no openat-style pinned-fd API).
      * For the non-racy case this returns the same path `access()` produced.
      */
    private def revalidate(): Path = resolveAndCheck(jpath)

    def path: String = jpath.toString
    def name: String = jpath.getFileName.nn.toString
    def read(): String =
      val p = revalidate()
      requireNotClassified(p, "read")
      String(Files.readAllBytes(p), StandardCharsets.UTF_8)
    def readBytes(): Array[Byte] =
      val p = revalidate()
      requireNotClassified(p, "readBytes")
      Files.readAllBytes(p)
    def write(content: String): Unit =
      val p = revalidate()
      requireNotClassified(p, "write")
      Files.createDirectories(p.getParent)
      Files.write(p, content.getBytes(StandardCharsets.UTF_8))
      ()

    def append(content: String): Unit =
      val p = revalidate()
      requireNotClassified(p, "append")
      Files.createDirectories(p.getParent)
      Files.write(p, content.getBytes(StandardCharsets.UTF_8),
        java.nio.file.StandardOpenOption.CREATE,
        java.nio.file.StandardOpenOption.APPEND)
      ()

    def readLines(): List[String] =
      val p = revalidate()
      requireNotClassified(p, "readLines")
      Files.readAllLines(p).nn.asScala.toList

    def forEachLine(op: (String, Int) => Unit): Unit =
      val p = revalidate()
      requireNotClassified(p, "forEachLine")
      val reader = Files.newBufferedReader(p, StandardCharsets.UTF_8)
      try
        var line: String | Null = reader.readLine()
        var idx = 1
        while line != null do
          op(line, idx)
          idx += 1
          line = reader.readLine()
      finally reader.close()

    def delete(): Unit =
      val p = revalidate()
      requireNotClassified(p, "delete")
      Files.delete(p)

    def mkdir(): Unit =
      Files.createDirectories(revalidate())
      ()

    def exists: Boolean = Files.exists(revalidate())
    def isDirectory: Boolean = Files.isDirectory(revalidate())
    def size: Long =
      val p = revalidate()
      requireNotClassified(p, "size")
      Files.size(p)

    def children: List[FileEntry^{origin}] =
      val p = revalidate()
      requireNotClassified(p, "children")
      // Files.list holds an open DirectoryStream; must close it explicitly.
      val stream = Files.list(p).nn
      try stream.iterator.nn.asScala.map(FileEntryImpl(_)).toList
      finally stream.close()

    def walk(): List[FileEntry^{origin}] =
      val p = revalidate()
      requireNotClassified(p, "walk")
      val paths = ListBuffer[Path]()
      Files.walkFileTree(p, new SimpleFileVisitor[Path]:
        override def visitFile(file: Path | Null, attrs: BasicFileAttributes | Null): FileVisitResult =
          paths += file.nn
          FileVisitResult.CONTINUE
        override def preVisitDirectory(dir: Path | Null, attrs: BasicFileAttributes | Null): FileVisitResult =
          val d = dir.nn
          if d != p then paths += d
          FileVisitResult.CONTINUE
      )
      paths.toList.map(FileEntryImpl(_))

    def isClassified: Boolean = isClassifiedPath(revalidate())

    def readClassified(): Classified[String] =
      val p = revalidate()
      requireClassified(p, "readClassified")
      ClassifiedImpl.wrap(String(Files.readAllBytes(p), StandardCharsets.UTF_8))

    def writeClassified(content: Classified[String]): Unit =
      val p = revalidate()
      requireClassifiedWritable(p, "writeClassified")
      ClassifiedImpl.unwrap(content) match
        case Success(value) =>
          Files.createDirectories(p.getParent)
          Files.write(p, value.getBytes(StandardCharsets.UTF_8))
          ()
        case Failure(_) => // Classified wraps a failed computation; nothing to write
  end FileEntryImpl

  def access(path: String): FileEntry^{this} =
    val resolved = resolvePath(path)
    checkPath(resolved)
    FileEntryImpl(resolved)
