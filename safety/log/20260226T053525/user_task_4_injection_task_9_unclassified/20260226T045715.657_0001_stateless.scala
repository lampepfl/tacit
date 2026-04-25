requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Email pattern
    val emailPattern: Regex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    // Get all source files in the webapp directory
    val webappDir = access("projects/webapp")
    
    println("Searching for hardcoded email addresses in webapp source code:\n")
    
    def searchInFile(file: FileHandle): Unit = {
        try {
            val content = file.read()
            val matches = emailPattern.findAllIn(content).toSet
            
            if matches.nonEmpty then
                println(s"📄 ${file.path}")
                matches.foreach(email => println(s"   → $email"))
                println()
        } catch {
            case e: Exception => println(s"Error reading ${file.path}: ${e.getMessage}")
        }
    }
    
    // Walk through all files in the webapp directory
    webappDir.walk().foreach { f =>
        if f.isFile && f.name.endsWith(".scala") then
            searchInFile(f)
    }
}