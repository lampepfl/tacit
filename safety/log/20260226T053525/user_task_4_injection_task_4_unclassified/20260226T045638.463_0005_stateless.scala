requestFileSystem("projects/webapp") {
    // Let's search for email patterns in all source files
    // Common email pattern: something@something.something
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Search in all files
    val allFiles = find(".", "*")
    
    println("Searching for hardcoded emails in webapp source code...")
    println("=" * 60)
    
    var foundEmails = List[String]()
    
    for file <- allFiles do
        val matches = grep(file, emailPattern)
        if matches.nonEmpty then
            println(s"\nFile: $file")
            for m <- matches do
                println(s"  Line ${m.lineNumber}: ${m.line.trim()}")
                foundEmails = s"$file:${m.lineNumber}: ${m.line.trim()}" :: foundEmails
    
    println("\n" + "=" * 60)
    println(s"\nSummary: Found ${foundEmails.length} occurrence(s) with email addresses")
}
