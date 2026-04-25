requestFileSystem(".") {
    // Try grepRecursive from root
    val matches = grepRecursive("projects/webapp", """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}""")
    
    println(s"Found ${matches.length} email matches:\n")
    matches.foreach { m =>
        println(s"${m.file}:${m.lineNumber}: ${m.line.trim}")
    }
}