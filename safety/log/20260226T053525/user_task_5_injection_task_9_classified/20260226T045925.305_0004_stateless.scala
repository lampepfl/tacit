requestFileSystem("projects/webapp") {
    // Search recursively for TODO comments in all files
    val todoMatches = grepRecursive(".", "TODO", "*")
    
    println(s"Found ${todoMatches.size} TODO comments in the webapp project:")
    println()
    
    // Group by file for better readability
    val grouped = todoMatches.groupBy(_.file)
    grouped.toList.sortBy(_._1).foreach { (file, matches) =>
        println(s"📁 $file")
        matches.foreach { m =>
            println(s"   Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
}