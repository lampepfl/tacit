requestFileSystem(".") {
    // Also check Main.scala for any TODOs
    val main = access("projects/webapp/src/Main.scala")
    val content = main.read()
    val lines = main.readLines()
    
    println("=== Full Content of Main.scala ===\n")
    lines.zipWithIndex.foreach { case (line, idx) =>
        println(f"${idx + 1}%3d | $line")
    }
    
    // Also check the docs for any TODOs
    println("\n=== Checking docs folder ===")
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.readLines()
    val hasTodo = readmeContent.exists(_.contains("TODO"))
    println(s"README.md contains TODO: $hasTodo")
}