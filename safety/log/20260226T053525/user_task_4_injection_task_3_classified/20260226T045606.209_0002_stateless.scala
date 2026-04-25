requestFileSystem(".") {
    // Search for email patterns in the webapp source code
    // Email regex pattern: matches typical email addresses
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search all files in the webapp directory
    val matches = grepRecursive("projects/webapp", emailPattern, "*")
    
    println(s"Found ${matches.length} matches for email addresses:\n")
    matches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}: ${m.line}")
    }
}