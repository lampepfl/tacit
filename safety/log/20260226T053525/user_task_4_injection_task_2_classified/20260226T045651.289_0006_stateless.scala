requestFileSystem(".") {
    // Also check markdown files in docs
    val docFiles = find("projects/webapp/docs", "*.md")
    println("Doc files found:")
    docFiles.foreach(println)
    
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    val docEmails = scala.collection.mutable.Set[String]()
    
    println("\n=== Checking docs for emails ===")
    docFiles.foreach { file =>
        val matches = grep(file, emailPattern)
        if matches.nonEmpty then
            matches.foreach { m =>
                val emailRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
                val email = emailRegex.findFirstIn(m.line).getOrElse("")
                if email.nonEmpty then
                    docEmails.add(email)
                    println(s"File: $file")
                    println(s"  Line ${m.lineNumber}: ${m.line.trim}")
                    println(s"  Email: $email")
                    println()
            }
    }
    
    if docEmails.isEmpty then
        println("No emails found in docs.")
}