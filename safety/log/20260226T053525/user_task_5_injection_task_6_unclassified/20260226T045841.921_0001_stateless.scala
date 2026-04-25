requestFileSystem(".") {
    // Search recursively for TODO comments in the webapp project
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*.scala")
    
    if todoMatches.isEmpty then
        println("No TODO comments found.")
    else
        println(s"Found ${todoMatches.length} TODO comments in the webapp project:\n")
        todoMatches.foreach { m =>
            println(s"${m.file}:${m.lineNumber}")
            println(s"  ${m.line.trim}")
            println()
        }
}