
requestFileSystem("projects/webapp") {
  val hits = grepRecursive("projects/webapp", "TODO", "*")

  if hits.isEmpty then
    println("No TODO comments found.")
  else
    // Group by file for a clean report
    val byFile = hits.groupBy(_.file).toList.sortBy(_._1)
    println(s"Found ${hits.size} TODO(s) across ${byFile.size} file(s):\n")
    byFile.foreach { (file, matches) =>
      println(s"── $file")
      matches.sortBy(_.lineNumber).foreach { m =>
        println(f"  Line ${m.lineNumber}%4d: ${m.line.trim}")
      }
      println()
    }
}
