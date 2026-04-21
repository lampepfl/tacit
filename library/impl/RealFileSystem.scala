package tacit.library

import language.experimental.captureChecking

import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters.*
import scala.util.{Success, Failure}

import java.nio.charset.StandardCharsets
import java.nio.file.{Files, FileVisitResult, Path, Paths, SimpleFileVisitor}
import java.nio.file.attribute.BasicFileAttributes

class RealFileSystem(
  val root: Path,
  check: String -> Boolean = _ => true,
  protected val classifiedPatterns: Set[String] = Set.empty
) extends BaseFileSystem:
  protected val normalizedRoot: Path =
    val abs = root.toAbsolutePath.normalize
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

  /** Resolves and validates that a path is within the allowed root.
    * Follows symlinks to prevent symlink-based escape attacks.
    */
  private def resolvePath(target: String): Path =
    val absPath = Paths.get(target).toAbsolutePath.normalize
    val resolved = resolveReal(absPath)
    if !resolved.startsWith(normalizedRoot) then
      throw SecurityException(
        s"Access denied: $resolved is outside root $normalizedRoot"
      )
    resolved

  private class FileEntryImpl(jpath: Path) extends FileEntry(this):
    def path: String = jpath.toString
    def name: String = jpath.getFileName.nn.toString
    def read(): String =
      requireNotClassified(jpath, "read")
      String(Files.readAllBytes(jpath), StandardCharsets.UTF_8)
    def readBytes(): Array[Byte] =
      requireNotClassified(jpath, "readBytes")
      Files.readAllBytes(jpath)
    def write(content: String): Unit =
      requireNotClassified(jpath, "write")
      Files.createDirectories(jpath.getParent)
      Files.write(jpath, content.getBytes(StandardCharsets.UTF_8))
      ()

    def append(content: String): Unit =
      requireNotClassified(jpath, "append")
      Files.createDirectories(jpath.getParent)
      Files.write(jpath, content.getBytes(StandardCharsets.UTF_8),
        java.nio.file.StandardOpenOption.CREATE,
        java.nio.file.StandardOpenOption.APPEND)
      ()

    def readLines(): List[String] =
      requireNotClassified(jpath, "readLines")
      Files.readAllLines(jpath).nn.asScala.toList

    def forEachLine(op: (String, Int) => Unit): Unit =
      requireNotClassified(jpath, "forEachLine")
      val reader = Files.newBufferedReader(jpath, StandardCharsets.UTF_8)
      try
        var line: String | Null = reader.readLine()
        var idx = 1
        while line != null do
          op(line, idx)
          idx += 1
          line = reader.readLine()
      finally reader.close()

    def delete(): Unit =
      requireNotClassified(jpath, "delete")
      Files.delete(jpath)

    def mkdir(): Unit =
      Files.createDirectories(jpath)
      ()

    def exists: Boolean = Files.exists(jpath)
    def isDirectory: Boolean = Files.isDirectory(jpath)
    def size: Long = Files.size(jpath)

    def children: List[FileEntry^{origin}] =
      requireNotClassified(jpath, "children")
      // Files.list holds an open DirectoryStream; must close it explicitly.
      val stream = Files.list(jpath).nn
      try stream.iterator.nn.asScala.map(FileEntryImpl(_)).toList
      finally stream.close()

    def walk(): List[FileEntry^{origin}] =
      requireNotClassified(jpath, "walk")
      val paths = ListBuffer[Path]()
      Files.walkFileTree(jpath, new SimpleFileVisitor[Path]:
        override def visitFile(file: Path | Null, attrs: BasicFileAttributes | Null): FileVisitResult =
          paths += file.nn
          FileVisitResult.CONTINUE
        override def preVisitDirectory(dir: Path | Null, attrs: BasicFileAttributes | Null): FileVisitResult =
          val d = dir.nn
          if d != jpath then paths += d
          FileVisitResult.CONTINUE
      )
      paths.toList.map(FileEntryImpl(_))

    def isClassified: Boolean = isClassifiedPath(jpath)

    def readClassified(): Classified[String] =
      requireClassified(jpath, "readClassified")
      ClassifiedImpl.wrap(String(Files.readAllBytes(jpath), StandardCharsets.UTF_8))

    def writeClassified(content: Classified[String]): Unit =
      requireClassified(jpath, "writeClassified")
      ClassifiedImpl.unwrap(content) match
        case Success(value) =>
          Files.createDirectories(jpath.getParent)
          Files.write(jpath, value.getBytes(StandardCharsets.UTF_8))
          ()
        case Failure(_) => // Classified wraps a failed computation; nothing to write
  end FileEntryImpl

  def access(path: String): FileEntry^{this} =
    val resolved = resolvePath(path)
    checkPath(resolved)
    FileEntryImpl(resolved)
