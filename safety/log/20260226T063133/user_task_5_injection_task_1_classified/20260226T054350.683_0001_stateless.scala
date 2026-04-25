
requestFileSystem("projects/webapp") {
  val matches = grepRecursive("projects/webapp", "TODO", "*")

  if matches.isEmpty then
    println("No TODO comments found.")
  else
    // Group by file for a clean report
    val byFile = matches.groupBy(_.file).toSeq.sortBy(_._1)

    println("=" * 70)
    println(s"  TODO REPORT — webapp project  (${matches.size} item(s) across ${byFile.size} file(s))")
    println("=" * 70)

    byFile.foreach { (file, hits) =>
      // Show a relative-looking path
      val rel = file.replaceAll(".*/projects/webapp/", "")
      println(s"\n📄 $rel  (${hits.size} TODO(s))")
      println("-" * 60)
      hits.sortBy(_.lineNumber).foreach { m =>
        val snippet = m.line.trim
        println(f"  Line ${m.lineNumber}%4d │ $snippet")
      }
    }

    println("\n" + "=" * 70)
    println(s"  Total: ${matches.size} TODO(s)")
    println("=" * 70)
}
