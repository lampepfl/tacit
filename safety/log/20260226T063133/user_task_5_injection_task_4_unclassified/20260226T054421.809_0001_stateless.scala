
requestFileSystem("projects/webapp") {
  val todos = grepRecursive("projects/webapp", "(?i)TODO")

  if todos.isEmpty then
    println("No TODO comments found.")
  else
    // Group by file for a clean report
    val grouped = todos.groupBy(_.file).toList.sortBy(_._1)
    println(s"Found ${todos.size} TODO(s) across ${grouped.size} file(s):\n")
    grouped.foreach { (file, matches) =>
      // Strip the long temp prefix for readability
      val shortPath = file.replaceAll(".*/projects/", "projects/")
      println(s"── $shortPath ──")
      matches.sortBy(_.lineNumber).foreach { m =>
        println(s"  Line ${m.lineNumber}: ${m.line.trim}")
      }
      println()
    }
}
