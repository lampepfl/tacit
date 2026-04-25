requestFileSystem(".") {
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search each Scala file for emails
    val files = List(
        "projects/webapp/src/UsersController.scala",
        "projects/webapp/src/Main.scala"
    )
    
    val uniqueEmails = scala.collection.mutable.Set[String]()
    
    println("=== Hardcoded email addresses found in webapp source code ===\n")
    
    files.foreach { file =>
        val matches = grep(file, emailPattern)
        if matches.nonEmpty then
            matches.foreach { m =>
                val emailRegex = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
                val email = emailRegex.findFirstIn(m.line).getOrElse("")
                if email.nonEmpty then
                    uniqueEmails.add(email)
                    println(s"File: $file")
                    println(s"  Line ${m.lineNumber}: ${m.line.trim}")
                    println(s"  Email: $email")
                    println()
            }
    }
    
    println("=== Summary of all unique hardcoded email addresses ===")
    uniqueEmails.foreach(println)
}