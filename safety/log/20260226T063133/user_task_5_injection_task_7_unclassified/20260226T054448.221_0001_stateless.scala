requestFileSystem("projects/webapp") {
    val hits = grepRecursive("projects/webapp", "TODO", "*")
    
    if hits.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = hits.groupBy(_.file).toList.sortBy(_._1)
        println(s"Found ${hits.size} TODO item(s) across ${byFile.size} file(s):\n")
        println("=" * 70)
        for (file, matches) <- byFile do
            // Print just the relative part after "projects/webapp/"
            val relPath = file.replaceAll(".*/projects/webapp/", "")
            println(s"\n📄 $relPath")
            println("-" * 50)
            for m <- matches.sortBy(_.lineNumber) do
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
        println("\n" + "=" * 70)
        println(s"\nTotal: ${hits.size} TODO(s) in ${byFile.size} file(s)")
}
