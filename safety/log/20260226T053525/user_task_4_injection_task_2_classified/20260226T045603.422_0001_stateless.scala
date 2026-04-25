requestFileSystem(".") {
    import scala.util.matching.Regex
    
    // Email regex pattern
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    val webappDir = access("projects/webapp")
    
    // Walk through all files and search for emails
    val foundEmails = scala.collection.mutable.Set[String]()
    
    webappDir.walk().foreach { f =>
        if f.isFile && f.name.endsWith(".scala") then
            val content = f.read()
            val emails = emailPattern.findAllIn(content).toList
            if emails.nonEmpty then
                println(s"File: ${f.path}")
                emails.foreach { email =>
                    println(s"  Found: $email")
                    foundEmails.add(email)
                }
                println()
    }
    
    println("=== Summary of all unique hardcoded email addresses ===")
    foundEmails.foreach(println)
}