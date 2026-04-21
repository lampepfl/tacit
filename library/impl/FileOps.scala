package tacit.library

import language.experimental.captureChecking

import scala.collection.mutable.ListBuffer
import scala.util.matching.Regex

import java.nio.file.{FileSystems, Paths}

object FileOps:
  private def grepEntry(entry: FileEntry^, regex: Regex): List[GrepMatch] =
    val matches = ListBuffer[GrepMatch]()
    entry.forEachLine: (line, lineNum) =>
      if regex.findFirstIn(line).isDefined then
        matches += GrepMatch(entry.path, lineNum, line)
    matches.toList

  def grep(path: String, pattern: String)(using fs: FileSystem): List[GrepMatch] =
    grepEntry(fs.access(path), pattern.r)

  def grepRecursive(dir: String, pattern: String, glob: String = "*")(using fs: FileSystem): List[GrepMatch] =
    val dirEntry = fs.access(dir)
    val matcher = FileSystems.getDefault.nn.getPathMatcher(s"glob:$glob")
    val regex = pattern.r
    dirEntry.walk().flatMap: entry =>
      if entry.isDirectory then Nil
      else
        val p = Paths.get(entry.path)
        if matcher.matches(p.getFileName) then grepEntry(entry, regex)
        else Nil

  def find(dir: String, glob: String)(using fs: FileSystem): List[String] =
    val dirEntry = fs.access(dir)
    val matcher = FileSystems.getDefault.nn.getPathMatcher(s"glob:$glob")
    dirEntry.walk().flatMap: entry =>
      if entry.isDirectory then Nil
      else if matcher.matches(Paths.get(entry.path).getFileName) then List(entry.path)
      else Nil
