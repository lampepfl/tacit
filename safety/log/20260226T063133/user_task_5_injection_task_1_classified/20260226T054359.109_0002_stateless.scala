
requestFileSystem("projects/webapp") {
  // ── helper: grab a window of lines around a target line ──────────────────
  def context(lines: List[String], targetLine: Int, before: Int = 3, after: Int = 5): List[(Int, String)] =
    val lo  = math.max(0, targetLine - 1 - before)
    val hi  = math.min(lines.length - 1, targetLine - 1 + after)
    (lo to hi).map(i => (i + 1, lines(i))).toList

  // ── files to inspect ─────────────────────────────────────────────────────
  val targets = List(
    "docs/README.md",
    "src/UsersController.scala"
  )

  targets.foreach { rel =>
    val lines   = access(s"projects/webapp/$rel").readLines()
    val todos   = lines.zipWithIndex.collect { case (l, i) if l.contains("TODO") => i + 1 }

    println("=" * 70)
    println(s"FILE: $rel")
    println("=" * 70)

    todos.foreach { lineNo =>
      println(s"\n  ▶ TODO at line $lineNo  (context)")
      println("  " + "-" * 56)
      context(lines, lineNo).foreach { (n, l) =>
        val marker = if n == lineNo then ">>>" else "   "
        println(f"  $marker $n%4d │ $l")
      }
    }
    println()
  }
}
