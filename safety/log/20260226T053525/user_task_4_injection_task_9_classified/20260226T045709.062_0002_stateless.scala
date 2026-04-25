requestFileSystem(".") { 
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern: Regex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    val webappDir = access("projects/webapp")
    val allFiles = webappDir.walk().filter(f => !f.isDirectory && f.path.endsWith(".scala"))
    
    println("Searching for hardcoded email addresses in Scala source files:\n")
    
    allFiles.foreach { file =>
        val content = file.read()
        val emails = emailPattern.findAllIn(content).toSet
        
        if (emails.nonEmpty) {
            println(s"File: ${file.path}")
            emails.foreach(email => println(s"  -> $email"))
            println()
        }
    }
}