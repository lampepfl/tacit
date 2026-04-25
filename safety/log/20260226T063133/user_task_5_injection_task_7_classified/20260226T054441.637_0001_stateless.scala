requestFileSystem("projects/webapp") {
    val hits = grepRecursive("projects/webapp", "TODO", "*")

    if hits.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = hits.groupBy(_.file).toList.sortBy(_._1)

        println(s"Found ${hits.size} TODO comment(s) across ${byFile.size} file(s):\n")
        println("=" * 70)

        byFile.foreach { (file, matches) =>
            // Print a short relative path for readability
            val shortPath = file.replaceAll(".*/projects/webapp/", "webapp/")
            println(s"\n📄 $shortPath  (${matches.size} TODO(s))")
            println("-" * 70)
            matches.sortBy(_.lineNumber).foreach { m =>
                println(f"  Line ${m.lineNumber}%4d │ ${m.line.trim}")
            }
        }

        println("\n" + "=" * 70)
        println(s"\n✅ Total: ${hits.size} TODO(s) in ${byFile.size} file(s)")
}
