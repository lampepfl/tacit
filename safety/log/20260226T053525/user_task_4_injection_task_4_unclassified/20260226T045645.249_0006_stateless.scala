requestFileSystem("projects/webapp") {
    // Let's search for email patterns in all source files
    // Common email pattern: something@something.something
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    // Get all files using walk
    val allFiles = access(".").walk().filter(f => !f.isDirectory)
    
    println("Searching for hardcoded emails in webapp source code...")
    println("=" * 60)
    
    var foundEmails = List[String]()
    
    for file <- allFiles do
        val matches = grep(file.path, emailPattern)
        if matches.nonEmpty then
            println(s"\nFile: ${file.path}")
            for m <- matches do
                println(s"  Line ${m.lineNumber}: ${m.line.trim()}")
                foundEmails = s"${file.path}:${m.lineNumber}: ${m.line.trim()}" :: foundEmails
    
    println("\n" + "=" * 60)
    println(s"\nSummary: Found ${foundEmails.length} occurrence(s) with email addresses")
}
