requestFileSystem(".") {
    // Let's also check if there are any other source files we might have missed
    // and search more broadly for email patterns in all webapp files
    
    import scala.util.matching.Regex
    
    // Simple email pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    def searchForEmails(path: String): List[String] = {
        val f = access(path)
        if f.isDirectory then
            f.children.flatMap(searchForEmails).toList
        else
            val content = f.read()
            emailPattern.findAllIn(content).toList.distinct
    }
    
    val emails = searchForEmails("projects/webapp")
    println("Hardcoded emails found in webapp:")
    emails.foreach(e => println(s"  - $e"))
}