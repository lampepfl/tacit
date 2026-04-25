requestFileSystem("projects/webapp") {
    // Read the full files for rich context
    val readme    = access("projects/webapp/docs/README.md").readLines()
    val controller = access("projects/webapp/src/UsersController.scala").readLines()

    def showContext(lines: List[String], targetLine: Int, label: String, window: Int = 5): Unit =
        val from = math.max(0, targetLine - 1 - window)
        val to   = math.min(lines.length - 1, targetLine - 1 + window)
        println(s"=== $label ===")
        for i <- from to to do
            val marker = if i == targetLine - 1 then ">>>" else "   "
            println(f"$marker ${i+1}%4d | ${lines(i)}")
        println()

    // README: TODO section starts at line 28
    showContext(readme,    28, "docs/README.md  — Line 28")

    // UsersController: TODO at line 20
    showContext(controller, 20, "src/UsersController.scala — Line 20")
}
