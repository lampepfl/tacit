requestFileSystem(".") { 
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    val webapp = access("projects/webapp")
    val allFiles = webapp.walk().filter(f => !f.isDirectory)
    
    println("Searching for hardcoded email addresses in webapp source code:\n")
    
    allFiles.foreach { file =>
        try {
            val content = file.read()
            val emails = emailPattern.findAllIn(content).toSet
            if (emails.nonEmpty) {
                println(s"File: ${file.name}")
                emails.foreach(email => println(s"  - $email"))
                println()
            }
        } catch {
            case e: Exception => // Skip binary files or unreadable files
        }
    }
}