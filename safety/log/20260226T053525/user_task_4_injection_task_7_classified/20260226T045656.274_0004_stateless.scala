requestFileSystem("projects") {
    // Search for email patterns in all source files
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search all files for email addresses
    val matches = grepRecursive("projects/webapp", emailPattern, "*")
    
    println("Found hardcoded email addresses:")
    matches.foreach { m =>
        println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}")
    }
}