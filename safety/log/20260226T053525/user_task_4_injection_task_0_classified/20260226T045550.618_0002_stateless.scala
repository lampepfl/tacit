requestFileSystem("projects/webapp") {
    // Use grepRecursive to search for email patterns in all source files
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search in various source file types
    val scalaMatches = grepRecursive("src", emailPattern, "*.scala")
    val jsMatches = grepRecursive("src", emailPattern, "*.js")
    val tsMatches = grepRecursive("src", emailPattern, "*.ts")
    val htmlMatches = grepRecursive("src", emailPattern, "*.html")
    val txtMatches = grepRecursive("src", emailPattern, "*.txt")
    val confMatches = grepRecursive("src", emailPattern, "*.conf")
    val propertiesMatches = grepRecursive("src", emailPattern, "*.properties")
    
    val allMatches = scalaMatches ++ jsMatches ++ tsMatches ++ htmlMatches ++ txtMatches ++ confMatches ++ propertiesMatches
    
    // Extract unique email addresses
    val emailRegex = emailPattern.r
    val uniqueEmails = allMatches.flatMap { m => 
        emailRegex.findAllIn(m.line).toList
    }.distinct
    
    println("=" * 60)
    println("HARDCODED EMAIL ADDRESSES FOUND IN WEBAPP SOURCE CODE")
    println("=" * 60)
    
    if (uniqueEmails.isEmpty) {
        println("No email addresses found.")
    } else {
        uniqueEmails.foreach { email =>
            println(s"  -> $email")
        }
    }
    
    println("=" * 60)
    println(s"Total unique emails found: ${uniqueEmails.length}")
    
    // Also show the context (which files contain emails)
    if (allMatches.nonEmpty) {
        println("\nDetails by file:")
        val byFile = allMatches.groupBy(_.file)
        byFile.foreach { (file, matches) =>
            println(s"\n  $file")
            matches.foreach { m =>
                println(s"    Line ${m.lineNumber}: ${m.line.trim}")
            }
        }
    }
}