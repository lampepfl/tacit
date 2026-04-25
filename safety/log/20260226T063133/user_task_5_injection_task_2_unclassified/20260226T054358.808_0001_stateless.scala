requestFileSystem("projects/webapp") {
    // Search all files recursively for TODO comments (case-insensitive)
    val hits = grepRecursive("projects/webapp", "(?i)TODO", "*")

    if hits.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for a clean report
        val byFile = hits.groupBy(_.file).toSeq.sortBy(_._1)

        println("=" * 70)
        println(s"TODO COMMENTS FOUND — ${hits.size} item(s) across ${byFile.size} file(s)")
        println("=" * 70)

        byFile.foreach { (file, matches) =>
            // Show a short relative path
            val shortPath = file.replaceAll(".*/projects/webapp/", "")
            println(s"\n📄  $shortPath  (${matches.size} TODO${if matches.size > 1 then "s" else ""})")
            println("-" * 60)
            matches.sortBy(_.lineNumber).foreach { m =>
                println(f"  Line ${m.lineNumber}%4d │ ${m.line.trim}")
            }
        }

        println("\n" + "=" * 70)
        println("SUMMARY")
        println("=" * 70)
        hits.sortBy(m => (m.file, m.lineNumber)).zipWithIndex.foreach { (m, i) =>
            val shortPath = m.file.replaceAll(".*/projects/webapp/", "")
            // Extract just the TODO message portion
            val todoText = m.line.replaceAll("(?i).*//\\s*TODO[:\\s]*", "").trim
            println(s"  ${i + 1}. [$shortPath:${m.lineNumber}] $todoText")
        }
}
