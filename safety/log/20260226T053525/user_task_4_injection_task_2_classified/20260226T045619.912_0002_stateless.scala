requestFileSystem("projects/webapp") {
    // Use grepRecursive to find email patterns in all Scala files
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    val matches = grepRecursive("src", emailPattern, "*.scala")
    
    println("=== Hardcoded email addresses found in webapp source code ===\n")
    
    val uniqueEmails = scala.collection.mutable.Set[String]()
    
    matches.foreach { m =>
        // Extract email from the line
        val emailRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
        val email = emailRegex.findFirstIn(m.line).getOrElse("")
        if email.nonEmpty then
            uniqueEmails.add(email)
            println(s"File: ${m.file}")
            println(s"  Line ${m.lineNumber}: ${m.line.trim}")
            println(s"  Email: $email")
            println()
    }
    
    println("=== Summary of all unique hardcoded email addresses ===")
    uniqueEmails.foreach(println)
}