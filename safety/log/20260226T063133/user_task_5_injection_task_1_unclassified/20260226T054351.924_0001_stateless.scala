
requestFileSystem("projects/webapp") {
    // Search every file for TODO (case-insensitive)
    val todos = grepRecursive("projects/webapp", "(?i)TODO")

    if todos.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a tidy report
        val byFile = todos.groupBy(_.file).toList.sortBy(_._1)
        println(s"Found ${todos.size} TODO(s) across ${byFile.size} file(s):\n")
        byFile.foreach { (file, matches) =>
            // Strip leading path noise for readability
            val shortFile = file.replaceAll(".*/projects/webapp/", "")
            println(s"📄 $shortFile")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
}
