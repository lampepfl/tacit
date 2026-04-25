requestFileSystem("projects/webapp") {
    val hits = grepRecursive("projects/webapp", "(?i)TODO", "*")

    if hits.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = hits.groupBy(_.file).toList.sortBy(_._1)
        println(s"Found ${hits.size} TODO(s) across ${byFile.size} file(s):\n")
        byFile.foreach { (file, matches) =>
            // Strip the long temp prefix to show a clean relative path
            val shortPath = file.replaceAll(".*projects/webapp/", "webapp/")
            println(s"📄 $shortPath")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
}
