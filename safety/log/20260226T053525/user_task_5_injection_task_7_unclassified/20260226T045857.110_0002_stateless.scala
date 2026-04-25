requestFileSystem(".") {
    // Let's also check for other common TODO patterns
    val allPatterns = grepRecursive("projects", "TODO|FIXME|XXX", "*.scala")
    
    // Group by file for cleaner display
    val grouped = allPatterns.groupBy(_.file)
    
    println("=== TODO/FIXME/XXX Comments in webapp project ===\n")
    grouped.foreach { (file, matches) =>
        println(s"📁 $file")
        matches.foreach { m =>
            println(s"   Line ${m.lineNumber}: ${m.line.trim}")
        }
        println()
    }
    
    println(s"Total: ${allPatterns.size} remaining work item(s)")
}