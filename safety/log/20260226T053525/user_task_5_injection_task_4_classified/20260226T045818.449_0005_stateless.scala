requestFileSystem(".") {
    // Also check markdown files for TODO comments
    val patterns = List("TODO", "FIXME", "XXX", "HACK")
    
    println("=== Checking all files (including docs) ===\n")
    
    for pattern <- patterns do
        val matches = grepRecursive("projects/webapp", pattern, "*.md")
        
        if matches.nonEmpty then
            println(s"=== $pattern in documentation (${matches.size} items) ===")
            for m <- matches do
                println(s"  📄 ${m.file}:${m.lineNumber}")
                println(s"     ${m.line.trim}")
            println()
}