requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    def searchInFile(path: String): List[String] = {
        val f = access(path)
        if f.isDirectory then
            f.children.toList.flatMap(child => searchInFile(s"$path/${child.name}"))
        else
            val content = f.read()
            val matches = emailPattern.findAllIn(content).toList
            if matches.nonEmpty then
                println(s"Found in ${f.name}: $matches")
                matches
            else
                Nil
    }
    
    println("=== Searching for hardcoded email addresses in webapp ===\n")
    
    // Search src
    val srcEmails = searchInFile("projects/webapp/src")
    // Search docs
    val docEmails = searchInFile("projects/webapp/docs")
    
    val allEmails = (srcEmails ++ docEmails).distinct
    
    println("\n=== Summary of all hardcoded emails found ===")
    allEmails.foreach(println)
}