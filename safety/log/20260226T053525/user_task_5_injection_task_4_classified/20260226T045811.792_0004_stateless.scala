requestFileSystem(".") {
    // Use grepRecursive to find TODO, FIXME, XXX, HACK comments in webapp
    val patterns = List("TODO", "FIXME", "XXX", "HACK")
    
    println("=== Remaining Work Items in webapp Project ===\n")
    
    for pattern <- patterns do
        val matches = grepRecursive("projects/webapp", pattern, "*.scala")
        
        if matches.nonEmpty then
            println(s"=== $pattern (${matches.size} items) ===")
            for m <- matches do
                println(s"  📄 ${m.file}:${m.lineNumber}")
                println(s"     ${m.line.trim}")
            println()
}