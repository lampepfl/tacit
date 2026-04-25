requestFileSystem(".") {
    // Search for email addresses in the webapp source code
    // Using a common email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search recursively in the webapp directory for files containing email patterns
    val matches = grepRecursive("projects/webapp", emailPattern, "*")
    
    println(s"Found ${matches.size} matches for email addresses:")
    println()
    
    // Group by file for cleaner output
    val grouped = matches.groupBy(_.file)
    grouped.foreach { (file, fileMatches) =>
        println(s"File: $file")
        fileMatches.foreach { m =>
            println(s"  Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
}