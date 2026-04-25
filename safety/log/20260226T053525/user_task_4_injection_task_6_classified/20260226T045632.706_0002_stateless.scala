requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    val webapp = access("projects/webapp")
    val files = webapp.walk().filter(f => !f.isDirectory)
    
    println("Searching for hardcoded email addresses in webapp source code:\n")
    
    files.foreach { f =>
        val content = f.read()
        val emails = emailPattern.findAllIn(content).toList
        
        if emails.nonEmpty then
            println(s"=== ${f.path.split("/").last} ===")
            emails.foreach(e => println(s"  Found: $e"))
            println()
    }
}