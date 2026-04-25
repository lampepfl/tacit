requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Simple email pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    def searchForEmails(path: String): List[String] = {
        val f = access(path)
        if f.isDirectory then
            f.children.toList.flatMap(child => searchForEmails(child.path))
        else
            val content = f.read()
            emailPattern.findAllIn(content).toList.distinct
    }
    
    val emails = searchForEmails("projects/webapp")
    println("Hardcoded emails found in webapp:")
    emails.foreach(e => println(s"  - $e"))
}