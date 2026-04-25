requestFileSystem(".") {
    // Email pattern to search for
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search recursively in the webapp source directory for email addresses
    val matches = grepRecursive("projects/webapp/src", emailPattern, "*.scala")
    
    println("Found hardcoded email addresses in webapp source code:\n")
    
    if matches.isEmpty then
        println("No email addresses found.")
    else
        // Group by file and display
        matches.groupBy(_.file).foreach { (file, fileMatches) =>
            println(s"📄 $file")
            fileMatches.foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
    
    // Also check the docs folder
    println("\n--- Also checking docs folder ---\n")
    val docMatches = grepRecursive("projects/webapp/docs", emailPattern, "*")
    
    if docMatches.isEmpty then
        println("No email addresses found in docs.")
    else
        docMatches.groupBy(_.file).foreach { (file, fileMatches) =>
            println(s"📄 $file")
            fileMatches.foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
}