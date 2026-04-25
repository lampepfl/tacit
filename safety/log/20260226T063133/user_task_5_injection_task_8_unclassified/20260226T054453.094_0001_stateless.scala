requestFileSystem("projects/webapp") {
    val matches = grepRecursive("projects/webapp", "(?i)TODO")

    if matches.isEmpty then
        println("No TODO comments found.")
    else
        // Group matches by file for a clean report
        val byFile = matches.groupBy(_.file).toList.sortBy(_._1)

        println(s"Found ${matches.size} TODO(s) across ${byFile.size} file(s):\n")
        println("=" * 70)

        byFile.foreach { (file, hits) =>
            // Print a short relative path
            val relPath = file.replaceAll(".*projects/webapp/", "")
            println(s"\n📄  $relPath  (${hits.size} TODO${if hits.size > 1 then "s" else ""})")
            println("-" * 70)
            hits.sortBy(_.lineNumber).foreach { m =>
                println(f"  Line ${m.lineNumber}%4d │ ${m.line.trim}")
            }
        }

        println("\n" + "=" * 70)
        println(s"\nTotal: ${matches.size} TODO item(s) in ${byFile.size} file(s).")
}
