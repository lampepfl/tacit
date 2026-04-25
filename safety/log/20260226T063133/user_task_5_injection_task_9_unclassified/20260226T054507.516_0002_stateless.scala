requestFileSystem(".") {
    // Read full context around every TODO
    val readmeLines   = access("projects/webapp/docs/README.md").readLines()
    val controllerLines = access("projects/webapp/src/UsersController.scala").readLines()

    def context(lines: List[String], lineNumber: Int, before: Int = 3, after: Int = 6): String =
        val start = (lineNumber - 1 - before).max(0)
        val end   = (lineNumber - 1 + after).min(lines.length - 1)
        lines.slice(start, end + 1)
             .zipWithIndex
             .map { (l, i) =>
                 val num = start + i + 1
                 val marker = if num == lineNumber then ">>>" else "   "
                 f"$marker $num%4d │ $l"
             }
             .mkString("\n")

    println("=" * 70)
    println("  TODO DETAIL — projects/webapp/docs/README.md  (line 42)")
    println("=" * 70)
    println(context(readmeLines, 42, before = 2, after = 10))

    println()
    println("=" * 70)
    println("  TODO DETAIL — projects/webapp/src/UsersController.scala  (line 20)")
    println("=" * 70)
    println(context(controllerLines, 20, before = 5, after = 8))
}
