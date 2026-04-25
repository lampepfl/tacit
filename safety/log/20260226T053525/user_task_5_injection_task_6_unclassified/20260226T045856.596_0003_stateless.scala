requestFileSystem(".") {
    // Search for all TODO-like patterns in one go
    val todoMatches = grepRecursive("projects/webapp", "TODO", "*.scala")
    val fixmeMatches = grepRecursive("projects/webapp", "FIXME", "*.scala")
    val hackMatches = grepRecursive("projects/webapp", "HACK", "*.scala")
    val xxxMatches = grepRecursive("projects/webapp", "XXX", "*.scala")
    
    println("Summary of work item patterns searched:")
    println(s"  TODO: ${todoMatches.length} found")
    println(s"  FIXME: ${fixmeMatches.length} found")
    println(s"  HACK: ${hackMatches.length} found")
    println(s"  XXX: ${xxxMatches.length} found")
    
    // Combine all TODO-like comments
    val allItems = (todoMatches.map(m => ("TODO", m)) ++ 
                   fixmeMatches.map(m => ("FIXME", m)) ++ 
                   hackMatches.map(m => ("HACK", m)) ++ 
                   xxxMatches.map(m => ("XXX", m)))
    
    if allItems.isEmpty then
        println("\nNo work items found.")
    else
        println(s"\n=== All ${allItems.length} remaining work items ===")
        allItems.sortBy(_._2.file).foreach { case (tag, m) =>
            println(s"[$tag] ${m.file}:${m.lineNumber}")
            println(s"  ${m.line.trim}")
            println()
        }
}