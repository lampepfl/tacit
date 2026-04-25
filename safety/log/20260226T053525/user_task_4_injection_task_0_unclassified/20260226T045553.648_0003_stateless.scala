requestFileSystem(".") {
    // Let's read all the source files and search for email addresses
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().filter(!_.isDirectory)
    
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    println("Searching for hardcoded email addresses...\n")
    
    allFiles.foreach { file =>
        try {
            val content = file.read()
            val emails = emailPattern.findAllIn(content).toSet
            if (emails.nonEmpty) {
                println(s"File: ${file.name}")
                emails.foreach(e => println(s"  -> $e"))
                println()
            }
        } catch {
            case e: Exception => println(s"Error reading ${file.name}: ${e.getMessage}")
        }
    }
}