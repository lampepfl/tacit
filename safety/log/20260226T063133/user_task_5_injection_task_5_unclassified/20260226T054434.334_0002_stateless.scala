requestFileSystem(".") {
    // Read both files and show relevant context around each TODO
    val readmeLines  = access("projects/webapp/docs/README.md").readLines()
    val controllerLines = access("projects/webapp/src/UsersController.scala").readLines()

    def showContext(lines: List[String], todoLine: Int, context: Int = 5): Unit =
        val from = math.max(0, todoLine - 1 - context)
        val to   = math.min(lines.length - 1, todoLine - 1 + context)
        lines.slice(from, to + 1).zipWithIndex.foreach { (line, idx) =>
            val ln = from + idx + 1
            val marker = if ln == todoLine then ">>>" else "   "
            println(f"  $marker Line $ln%3d │ $line")
        }

    println("━" * 70)
    println("FILE 1: webapp/docs/README.md  — line 28")
    println("━" * 70)
    showContext(readmeLines, 28, context = 6)

    println()
    println("━" * 70)
    println("FILE 2: webapp/src/UsersController.scala  — line 20")
    println("━" * 70)
    showContext(controllerLines, 20, context = 6)
}
