requestFileSystem("projects") {
    // Let's also check the documentation files for any emails
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search in all files including docs
    val matches = grepRecursive("projects/webapp", emailPattern, "*")
    
    // Let's also print the unique emails found
    val uniqueEmails = matches.map { m =>
        m.line.collect { case e if e.toString.contains("@") => e.toString }
    }.flatten.distinct
    
    println("=== All hardcoded email addresses found ===")
    matches.foreach { m =>
        // Extract just the email from the line
        val email = m.line.replaceAll(s".*($emailPattern).*", "$1")
        println(s"  $email")
    }
}