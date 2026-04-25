requestFileSystem(".") {
    val matches = grepRecursive("projects/webapp", "TODO", "*")

    if matches.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val grouped = matches.groupBy(_.file).toList.sortBy(_._1)

        println(s"Found ${matches.size} TODO(s) across ${grouped.size} file(s):\n")
        println("=" * 70)

        grouped.foreach { (file, hits) =>
            // Strip the long temp prefix for readability
            val shortPath = file.replaceAll(".*/projects/webapp/", "webapp/")
            println(s"\n📄  $shortPath  (${hits.size} TODO(s))")
            println("-" * 70)
            hits.sortBy(_.lineNumber).foreach { m =>
                val snippet = m.line.trim
                println(f"  Line ${m.lineNumber}%4d │ $snippet")
            }
        }

        println("\n" + "=" * 70)
        println(s"\n✅  Total: ${matches.size} TODO item(s) remaining.")
}
