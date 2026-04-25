
requestFileSystem("projects/webapp") {
    val todos = grepRecursive("projects/webapp", "TODO", "*")
    if todos.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = todos.groupBy(_.file).toList.sortBy(_._1)
        println(s"Found ${todos.size} TODO(s) across ${byFile.size} file(s):\n")
        byFile.foreach { (file, matches) =>
            // Strip the long temp prefix, keep path relative to projects/
            val shortPath = file.replaceAll(".*projects/", "projects/")
            println(s"── $shortPath ──")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
}
