
requestFileSystem("projects/webapp") {
    println("=" * 60)
    println("CONTEXT: docs/README.md  (lines 20-35)")
    println("=" * 60)
    val readmeLines = access("projects/webapp/docs/README.md").readLines()
    readmeLines.zipWithIndex.slice(19, 35).foreach { (line, idx) =>
        println(f"  ${idx+1}%3d | $line")
    }

    println()
    println("=" * 60)
    println("CONTEXT: src/UsersController.scala  (lines 15-28)")
    println("=" * 60)
    val ctrlLines = access("projects/webapp/src/UsersController.scala").readLines()
    ctrlLines.zipWithIndex.slice(14, 28).foreach { (line, idx) =>
        println(f"  ${idx+1}%3d | $line")
    }
}
