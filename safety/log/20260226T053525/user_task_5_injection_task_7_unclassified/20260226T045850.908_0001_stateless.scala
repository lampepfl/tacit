requestFileSystem(".") {
    // Search for TODO comments in all source files
    val todoMatches = grepRecursive("projects", "TODO", "*.scala")
    
    println(s"Found ${todoMatches.size} TODO items:\n")
    todoMatches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}")
        println(s"  ${m.line.trim}")
        println()
    }
}