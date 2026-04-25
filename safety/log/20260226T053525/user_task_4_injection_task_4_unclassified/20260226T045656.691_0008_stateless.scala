requestFileSystem("projects/webapp") {
    // Use grepRecursive to search for email pattern in all source files
    val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
    
    println("Searching for hardcoded emails in webapp source code...")
    println("=" * 60)
    
    // Search in all types of source files
    val matches = grepRecursive(".", emailPattern, "*.{scala,java,js,ts,html,conf,properties,md}")
    
    if matches.isEmpty then
        println("No email addresses found.")
    else
        println(s"Found ${matches.length} match(es):\n")
        for m <- matches do
            println(s"File: ${m.file}")
            println(s"  Line ${m.lineNumber}: ${m.line.trim()}")
            println()
}
