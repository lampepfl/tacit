package tacit.library

import language.experimental.captureChecking
import caps.assumeSafe

import scala.collection.concurrent.TrieMap
import scala.util.{Success, Failure}
import java.nio.charset.StandardCharsets
import java.nio.file.{Path, Paths}

@assumeSafe
class VirtualFileSystem(
  val root: Path,
  check: String -> Boolean = _ => true,
  initialFiles: Map[String, String] = Map.empty,
  protected val classifiedPatterns: Set[String] = Set.empty
) extends BaseFileSystem:
  protected val normalizedRoot: Path = root.toAbsolutePath.normalize
  protected def pathCheck(relativePath: String): Boolean = check(relativePath)
  private val files: TrieMap[Path, Array[Byte]] = TrieMap.empty
  private val directories: TrieMap[Path, Unit] = TrieMap(normalizedRoot -> ())

  initialFiles.foreach { (relPath, content) =>
    val resolved = normalizedRoot.resolve(relPath).normalize
    ensureParentDirs(resolved)
    files(resolved) = content.getBytes(StandardCharsets.UTF_8)
  }

  private def ensureParentDirs(path: Path): Unit =
    var parent: Path | Null = path.getParent
    while parent != null do
      directories(parent) = ()
      parent = parent.getParent

  private def resolvePath(target: String): Path =
    val resolved = Paths.get(target).toAbsolutePath.normalize
    if !resolved.startsWith(normalizedRoot) then
      throw SecurityException(
        s"Access denied: $resolved is outside virtual root $normalizedRoot"
      )
    resolved

  private class FileEntryImpl(resolved: Path) extends FileEntry(this):
    def path: String = resolved.toString
    def name: String = resolved.getFileName.nn.toString
    def read(): String =
      requireNotClassified(resolved, "read")
      String(readBytes(), StandardCharsets.UTF_8)
    private def getOrThrow(path: Path): Array[Byte] =
      files.getOrElse(path, throw java.nio.file.NoSuchFileException(path.toString))

    def readBytes(): Array[Byte] =
      requireNotClassified(resolved, "readBytes")
      getOrThrow(resolved)

    def write(content: String): Unit =
      requireNotClassified(resolved, "write")
      ensureParentDirs(resolved)
      files(resolved) = content.getBytes(StandardCharsets.UTF_8)

    def append(content: String): Unit =
      requireNotClassified(resolved, "append")
      ensureParentDirs(resolved)
      files.updateWith(resolved):
        case Some(old) => Some(old ++ content.getBytes(StandardCharsets.UTF_8))
        case None      => Some(content.getBytes(StandardCharsets.UTF_8))

    def readLines(): List[String] =
      requireNotClassified(resolved, "readLines")
      val raw = getOrThrow(resolved)
      val content = String(raw, StandardCharsets.UTF_8)
      content.linesIterator.toList

    def forEachLine(op: (String, Int) => Unit): Unit =
      requireNotClassified(resolved, "forEachLine")
      val raw = getOrThrow(resolved)
      val content = String(raw, StandardCharsets.UTF_8)
      content.linesIterator.zipWithIndex.foreach((line, idx) => op(line, idx + 1))

    def delete(): Unit =
      requireNotClassified(resolved, "delete")
      if !files.contains(resolved) then
        throw java.nio.file.NoSuchFileException(resolved.toString)
      files.remove(resolved)
      ()

    def mkdir(): Unit =
      ensureParentDirs(resolved)
      directories(resolved) = ()

    def exists: Boolean =
      files.contains(resolved) || directories.contains(resolved)

    def isDirectory: Boolean = directories.contains(resolved)

    def size: Long =
      files.get(resolved).map(_.length.toLong).getOrElse(0L)

    def children: List[FileEntry^{origin}] =
      requireNotClassified(resolved, "children")
      if !directories.contains(resolved) then
        throw java.nio.file.NoSuchFileException(resolved.toString)
      val childFiles = files.keys.filter { p =>
        val parent = p.getParent
        parent != null && parent.nn == resolved
      }
      val childDirs = directories.keys.filter { d =>
        val parent = d.getParent
        d != resolved && parent != null && parent.nn == resolved
      }
      (childFiles ++ childDirs).toList.map(p => new FileEntryImpl(p)).sortBy(_.path)

    def walk(): List[FileEntry^{origin}] =
      requireNotClassified(resolved, "walk")
      val allPaths = directories.keys.filter(d => d.startsWith(resolved) && d != resolved) ++
        files.keys.filter(_.startsWith(resolved))
      allPaths.toList.map(p => new FileEntryImpl(p))

    def isClassified: Boolean = isClassifiedPath(resolved)

    def readClassified(): Classified[String] =
      requireClassified(resolved, "readClassified")
      val bytes = files.getOrElse(resolved, throw java.nio.file.NoSuchFileException(resolved.toString))
      ClassifiedImpl.wrap(String(bytes, StandardCharsets.UTF_8))

    def writeClassified(content: Classified[String]): Unit =
      requireClassified(resolved, "writeClassified")
      ClassifiedImpl.unwrap(content) match
        case Success(value) =>
          ensureParentDirs(resolved)
          files(resolved) = value.getBytes(StandardCharsets.UTF_8)
        case Failure(_) => // Classified wraps a failed computation; nothing to write
  end FileEntryImpl

  def access(path: String): FileEntry^{this} =
    val resolved = resolvePath(path)
    checkPath(resolved)
    new FileEntryImpl(resolved)
