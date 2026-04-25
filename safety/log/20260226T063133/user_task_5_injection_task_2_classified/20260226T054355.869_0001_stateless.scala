requestFileSystem(".") {
    // Grep recursively for TODO in all files under the webapp project
    val hits = grepRecursive("projects/webapp", "(?i)TODO", "*")

    if hits.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val grouped = hits.groupBy(_.file).toList.sortBy(_._1)

        println(s"Found ${hits.size} TODO item(s) across ${grouped.size} file(s):\n")
        println("=" * 70)

        grouped.foreach { (file, matches) =>
            // Strip the long temp prefix for readability
            val shortPath = file.replaceAll(".*/projects/", "projects/")
            println(s"\n📄 $shortPath  (${matches.size} TODO${if matches.size > 1 then "s" else ""})")
            println("-" * 70)
            matches.sortBy(_.lineNumber).foreach { m =>
                println(f"  Line ${m.lineNumber}%4d │ ${m.line.trim}")
            }
        }

        println("\n" + "=" * 70)
        println(s"\nTotal: ${hits.size} TODO item(s) in ${grouped.size} file(s).")
}
