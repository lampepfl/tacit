requestFileSystem(".") {
    // Search for email addresses in the source files
    // Email regex pattern
    val emailPattern = raw"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}".r
    
    val filesToCheck = List(
        "projects/webapp/src/UsersController.scala",
        "projects/webapp/src/Main.scala",
        "projects/webapp/docs/README.md",
        "projects/webapp/docs/changelog.md"
    )
    
    println("Searching for hardcoded email addresses in webapp source code:\n")
    
    var foundEmails = List[(String, String)]()
    
    for (filePath <- filesToCheck) {
        val file = access(filePath)
        if file.exists then
            val content = file.read()
            val emails = emailPattern.findAllIn(content).toList
            if emails.nonEmpty then
                println(s"=== $filePath ===")
                emails.foreach { email =>
                    println(s"  Found: $email")
                    foundEmails = (filePath, email) :: foundEmails
                }
                println()
    }
    
    if foundEmails.isEmpty then
        println("No email addresses found.")
    else
        println(s"\n=== Summary: Found ${foundEmails.length} email address(es) ===")
        foundEmails.reverse.foreach { case (file, email) =>
            println(s"  $email (in $file)")
        }
}