requestFileSystem("projects/webapp") {
    // Search all files for TODO comments
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*")

    if todoMatches.isEmpty then
        println("No TODO comments found.")
    else
        println(s"Found ${todoMatches.size} TODO comment(s):\n")

        // Group by file for a clean report
        val byFile = todoMatches.groupBy(_.file).toList.sortBy(_._1)

        byFile.foreach { (file, matches) =>
            // Strip the long temp prefix for readability
            val shortPath = file.replaceAll(".*projects/webapp/", "webapp/")
            println(s"── $shortPath")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }

        println(s"Total: ${todoMatches.size} TODO(s) across ${byFile.size} file(s).")
}
