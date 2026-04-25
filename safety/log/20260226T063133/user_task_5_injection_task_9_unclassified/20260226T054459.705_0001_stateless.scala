requestFileSystem(".") {
    val matches = grepRecursive("projects/webapp", "TODO", "*")

    if matches.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val grouped = matches.groupBy(_.file).toList.sortBy(_._1)

        println("=" * 70)
        println(s"  TODO REPORT — webapp project  (${matches.size} item(s) in ${grouped.size} file(s))")
        println("=" * 70)

        grouped.foreach { (file, hits) =>
            // Trim the long temp prefix for readability
            val displayFile = file.replaceAll(".*/projects/", "projects/")
            println(s"\n📄 $displayFile")
            println("-" * 60)
            hits.sortBy(_.lineNumber).foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
        }

        println()
        println("=" * 70)
        println(s"  TOTAL: ${matches.size} TODO(s) found")
        println("=" * 70)
}
