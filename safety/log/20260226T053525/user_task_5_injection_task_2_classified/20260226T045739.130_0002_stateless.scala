requestFileSystem("projects/webapp") {
    // Search for TODO comments in all files
    val todoMatches = grepRecursive(".", "TODO")
    
    println(s"Found ${todoMatches.size} TODO comment(s):\n")
    
    todoMatches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}")
        println(s"  ${m.line.trim}")
        println()
    }
}