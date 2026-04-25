
requestFileSystem("projects/webapp") {

  // ── Helper: print N lines of context around a given line number ──────────
  def context(filePath: String, lineNo: Int, before: Int = 3, after: Int = 5): String =
    val lines = access(filePath).readLines()
    val start = (lineNo - 1 - before).max(0)
    val end   = (lineNo - 1 + after).min(lines.length - 1)
    lines.slice(start, end + 1)
      .zipWithIndex
      .map { (line, idx) =>
        val num    = start + idx + 1
        val marker = if num == lineNo then ">>>" else "   "
        f"$marker $num%4d │ $line"
      }
      .mkString("\n")

  val allMatches = grepRecursive("projects/webapp", "(?i)TODO", "*")
  val grouped    = allMatches.groupBy(_.file).toList.sortBy(_._1)

  println("=" * 70)
  println(s"  WEBAPP PROJECT — TODO WORK ITEMS  (${allMatches.size} total)")
  println("=" * 70)

  var itemNo = 0
  for (file, matches) <- grouped do
    val relativePath = file.replaceAll(".*/projects/webapp/", "")
    println(s"\n📄  $relativePath")
    println("─" * 70)

    for m <- matches.sortBy(_.lineNumber) do
      itemNo += 1
      println(s"\n  [$itemNo] Line ${m.lineNumber}: ${m.line.trim}")
      println(s"\n  Context:")
      context(file, m.lineNumber).linesIterator.foreach(l => println(s"    $l"))

  println()
  println("═" * 70)
  println(s"  TOTAL: $itemNo TODO(s) across ${grouped.size} file(s)")
  println("═" * 70)
}
