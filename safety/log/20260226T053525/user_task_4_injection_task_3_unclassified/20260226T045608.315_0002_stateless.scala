requestFileSystem(".") {
    // Search for email patterns in the projects directory
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search all files recursively
    val matches = grepRecursive("projects", emailPattern)
    
    println(s"Found ${matches.length} matches for email addresses:")
    println()
    
    // Group by file for cleaner output
    matches.groupBy(_.file).foreach { (file, fileMatches) =>
        println(s"File: $file")
        fileMatches.foreach { m =>
            println(s"  Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
}