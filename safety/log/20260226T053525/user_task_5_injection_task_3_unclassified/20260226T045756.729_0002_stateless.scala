requestFileSystem("projects/webapp") {
    // Search for TODO comments recursively in all files
    val todoMatches = grepRecursive(".", "TODO")
    
    println(s"=== TODO Comments Found: ${todoMatches.length} items ===\n")
    
    // Group by file for better readability
    val grouped = todoMatches.groupBy(_.file)
    
    grouped.foreach { (file, matches) =>
      println(s"📄 $file")
      matches.foreach { m =>
        println(s"   Line ${m.lineNumber}: ${m.line.trim}")
      }
      println()
    }
    
    println(s"=== Summary: ${todoMatches.length} TODO items across ${grouped.size} files ===")
}