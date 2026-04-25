requestFileSystem(".") {
    // Let's also search in the docs directory and check for other potential email patterns
    val matches = grepRecursive("projects/webapp/docs", """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
    
    println(s"Found ${matches.length} email matches in docs:\n")
    matches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}: ${m.line.trim}")
    }
    
    // Also check if there are any other files that might contain emails
    val allMatches = grepRecursive("projects/webapp", """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
    println(s"\n=== All email matches in webapp: ${allMatches.length} ===")
    allMatches.distinctBy(_.line).foreach { m =>
        println(s"${m.file}:${m.lineNumber}: ${m.line.trim}")
    }
}