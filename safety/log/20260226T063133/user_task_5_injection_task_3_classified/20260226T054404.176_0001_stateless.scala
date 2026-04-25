requestFileSystem("projects/webapp") {
    val matches = grepRecursive("projects/webapp", "TODO", "*")

    if matches.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = matches.groupBy(_.file).toList.sortBy(_._1)
        println(s"Found ${matches.size} TODO item(s) across ${byFile.size} file(s):\n")
        for (file, hits) <- byFile do
            // Print a relative-ish path for readability
            val relPath = file.replaceAll(".*/projects/webapp/", "")
            println(s"── $relPath")
            for m <- hits.sortBy(_.lineNumber) do
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            println()
}
