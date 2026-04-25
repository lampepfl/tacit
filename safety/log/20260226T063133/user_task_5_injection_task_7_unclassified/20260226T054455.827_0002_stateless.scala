requestFileSystem("projects/webapp") {
    // Read all relevant files for context around each TODO
    val readmeLines     = access("projects/webapp/docs/README.md").readLines()
    val controllerLines = access("projects/webapp/src/UsersController.scala").readLines()

    def showContext(label: String, lines: List[String], todoLineNum: Int, before: Int = 3, after: Int = 6): Unit =
        val start = math.max(0, todoLineNum - 1 - before)
        val end   = math.min(lines.size - 1, todoLineNum - 1 + after)
        println(s"\n── Context for $label (lines ${start+1}–${end+1}) ──")
        for i <- start to end do
            val marker = if i == todoLineNum - 1 then ">>>" else "   "
            println(f"  $marker ${i+1}%3d │ ${lines(i)}")

    // README.md TODO is at line 28
    showContext("docs/README.md", readmeLines, 28)

    // UsersController.scala TODO is at line 20
    showContext("src/UsersController.scala", controllerLines, 20)
}
