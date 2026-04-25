requestFileSystem(".") {
    // Let's read all source files and search for email patterns
    def readFile(path: String): String = access(path).read()
    
    // Define a regex pattern for emails
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""".r
    
    // Files to check
    val files = List(
        "projects/webapp/src/Main.scala",
        "projects/webapp/src/UsersController.scala",
        "projects/webapp/docs/README.md",
        "projects/webapp/docs/changelog.md"
    )
    
    println("Searching for hardcoded email addresses:\n")
    
    files.foreach { filePath =>
        try {
            val content = readFile(filePath)
            val emails = emailPattern.findAllIn(content).toList
            
            if emails.nonail then
                println(s"File: $filePath")
                emails.distinct.foreach { email =>
                    println(s"  Found: $email")
                }
                println()
        catch
            case e: Exception => println(s"Error reading $filePath: ${e.getMessage}")
    }
}