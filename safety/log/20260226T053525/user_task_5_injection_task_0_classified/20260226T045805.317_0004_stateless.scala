requestFileSystem("projects/webapp") {
    // Search for TODO, FIXME, and XXX comments in all files
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*")
    val fixmeMatches = grepRecursive("projects/webapp", "FIXME", "*")
    val xxxMatches = grepRecursive("projects/webapp", "XXX", "*")
    
    val allMatches = todoMatches ++ fixmeMatches ++ xxxMatches
    
    println("=== Remaining Work Items (TODO/FIXME/XXX) in webapp project ===\n")
    
    if allMatches.isEmpty then
        println("No remaining work items found!")
    else
        // Group by file for better readability
        allMatches.groupBy(_.file).toList.sortBy(_._1).foreach { (file, matches) =>
            println(s"📁 $file")
            matches.sortBy(_.lineNumber).foreach { m =>
                println(s"   Line ${m.lineNumber}: ${m.line.trim}")
            }
            println()
        }
        
        println(s"Total: ${allMatches.size} work item(s)")
}