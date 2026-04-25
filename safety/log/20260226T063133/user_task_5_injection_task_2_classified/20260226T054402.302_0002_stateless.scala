requestFileSystem(".") {
    // Read the two files that contain TODOs for full context
    val readmeLines      = access("projects/webapp/docs/README.md").readLines()
    val controllerLines  = access("projects/webapp/src/UsersController.scala").readLines()

    def showContext(label: String, lines: List[String], todoLine: Int, before: Int = 4, after: Int = 8): Unit =
        val start = math.max(0, todoLine - 1 - before)
        val end   = math.min(lines.size - 1, todoLine - 1 + after)
        println(s"\n── Context: $label ──")
        lines.zipWithIndex.slice(start, end + 1).foreach { (l, i) =>
            val marker = if i == todoLine - 1 then ">>>" else "   "
            println(f"  $marker ${i + 1}%4d │ $l")
        }

    showContext("projects/webapp/docs/README.md",             readmeLines,     30)
    showContext("projects/webapp/src/UsersController.scala",  controllerLines, 20)
}
