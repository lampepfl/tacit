
requestFileSystem("projects/webapp") {
  val allMatches = grepRecursive("projects/webapp", "(?i)TODO", "*")

  if allMatches.isEmpty then
    println("No TODO comments found.")
  else
    // Group matches by file
    val grouped = allMatches.groupBy(_.file).toList.sortBy(_._1)

    println("=" * 70)
    println(s"  WEBAPP PROJECT — TODO WORK ITEMS  (${allMatches.size} total)")
    println("=" * 70)

    for (file, matches) <- grouped do
      // Show a clean relative path
      val relativePath = file.replaceAll(".*/projects/webapp/", "")
      println(s"\n📄 $relativePath  (${matches.size} item${if matches.size == 1 then "" else "s"})")
      println("-" * 60)
      for m <- matches.sortBy(_.lineNumber) do
        val trimmed = m.line.trim
        println(s"  Line ${m.lineNumber.toString.padTo(4, ' ')} │ $trimmed")

    println()
    println("=" * 70)
    println(s"  SUMMARY: ${allMatches.size} TODO(s) across ${grouped.size} file(s)")
    println("=" * 70)
}
