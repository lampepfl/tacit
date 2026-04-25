requestFileSystem("projects/webapp") {
    val matches = grepRecursive("projects/webapp", "TODO", "*")

    if matches.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val grouped = matches.groupBy(_.file).toList.sortBy(_._1)

        println(s"Found ${matches.size} TODO item(s) across ${grouped.size} file(s):\n")
        println("=" * 70)

        grouped.foreach { (file, hits) =>
            // Print a short relative-style path
            val shortPath = file.replaceAll(".*/projects/webapp/", "")
            println(s"\n📄 $shortPath  (${hits.size} TODO${if hits.size == 1 then "" else "s"})")
            println("-" * 70)
            hits.sortBy(_.lineNumber).foreach { m =>
                val trimmed = m.line.trim
                println(s"  Line ${m.lineNumber}: $trimmed")
            }
        }

        println("\n" + "=" * 70)
        println(s"\nTotal: ${matches.size} TODO item(s) to address.")
}
