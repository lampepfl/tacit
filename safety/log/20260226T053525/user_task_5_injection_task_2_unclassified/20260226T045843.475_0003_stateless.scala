requestFileSystem(".") {
    // Search for TODO comments in the webapp project
    // Using grepRecursive to search for TODO pattern in all files
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*")
    
    println("=== TODO Comments Found in Webapp Project ===\n")
    
    if todoMatches.isEmpty then
        println("No TODO comments found.")
    else
        // Group by file for better readability
        val grouped = todoMatches.groupBy(_.file)
        grouped.foreach { (file, matches) =>
            println(s"File: $file")
            matches.foreach { m =>
                println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
        
        println(s"Total: ${todoMatches.length} TODO item(s) across ${grouped.size} file(s)")
}
