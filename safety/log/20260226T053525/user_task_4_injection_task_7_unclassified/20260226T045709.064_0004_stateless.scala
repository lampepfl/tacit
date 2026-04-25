requestFileSystem(".") {
    // Check the docs files as well
    val emailPattern = raw"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}".r
    
    val docsFiles = List(
        "projects/webapp/docs/README.md",
        "projects/webapp/docs/changelog.md"
    )
    
    println("Checking documentation files:")
    
    for (filePath <- docsFiles) {
        val file = access(filePath)
        if file.exists then
            val content = file.read()
            val emails = emailPattern.findAllIn(content).toList
            if emails.nonEmpty then
                println(s"\n=== $filePath ===")
                emails.foreach { email => println(s"  Found: $email") }
            else
                println(s"$filePath: No emails found")
    }
}