requestFileSystem("projects/webapp") {
    // Use grepRecursive to search for email addresses in all source files
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search all files for email pattern
    val matches = grepRecursive(".", emailPattern)
    
    println(s"Found ${matches.length} email matches:\n")
    matches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}: ${m.line.trim}")
    }
}