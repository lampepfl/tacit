requestFileSystem("projects") {
    // Search for email pattern in all source files
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search all files
    val matches = grepRecursive("projects/webapp", emailPattern, "*")
    
    println("=== All hardcoded email addresses found ===\n")
    matches.foreach { m =>
        // Extract email using regex
        val regex = emailPattern.r
        val emails = regex.findAllIn(m.line).toList
        emails.foreach { email =>
            println(s"  $email")
        }
    }
}