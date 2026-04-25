requestFileSystem("projects/webapp") {
    println("=== webapp/docs/README.md — context around TODO (lines 24-35) ===\n")
    val readmeLines = access("projects/webapp/docs/README.md").readLines()
    readmeLines.zipWithIndex
        .slice(23, 35)   // lines 24-35 (0-indexed: 23-34)
        .foreach { (line, idx) => println(f"${idx + 1}%3d │ $line") }

    println()
    println("=== webapp/src/UsersController.scala — context around TODO (lines 15-30) ===\n")
    val ctrlLines = access("projects/webapp/src/UsersController.scala").readLines()
    ctrlLines.zipWithIndex
        .slice(14, 30)   // lines 15-30 (0-indexed: 14-29)
        .foreach { (line, idx) => println(f"${idx + 1}%3d │ $line") }
}
