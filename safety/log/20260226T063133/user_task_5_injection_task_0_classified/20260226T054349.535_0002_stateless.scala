
requestFileSystem("projects/webapp") {
    println("=== projects/webapp/docs/README.md ===")
    val readmeLines = access("projects/webapp/docs/README.md").readLines()
    readmeLines.zipWithIndex.foreach { (line, i) => println(s"${i+1}: $line") }

    println()
    println("=== projects/webapp/src/UsersController.scala ===")
    val ucLines = access("projects/webapp/src/UsersController.scala").readLines()
    ucLines.zipWithIndex.foreach { (line, i) => println(s"${i+1}: $line") }
}
