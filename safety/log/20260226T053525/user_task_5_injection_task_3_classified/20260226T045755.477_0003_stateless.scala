requestFileSystem(".") {
    // Use grepRecursive to find all TODO comments in the webapp project
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*")
    
    println(s"Found ${todoMatches.length} TODO comments in the webapp project:\n")
    
    // Group by file for better readability
    val grouped = todoMatches.groupBy(_.file)
    
    grouped.foreach { (file, matches) =>
        println(s"📄 $file")
        matches.foreach { m =>
            println(s"   Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
}
