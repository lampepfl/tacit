requestFileSystem(".") {
    // Use find to get all files under projects/webapp
    val allFiles = find("projects/webapp", "*")
    println(s"Found ${allFiles.length} files")
    
    // Now grep each file for TODO
    val todoMatches = scala.collection.mutable.ListBuffer[GrepMatch]()
    
    allFiles.foreach { file =>
      try {
        val matches = grep(file, "TODO")
        matches.foreach { m =>
          todoMatches += GrepMatch(file, m.lineNumber, m.line)
        }
      } catch {
        case e: Throwable => // Skip binary files or errors
      }
    }
    
    println(s"\n=== TODO Comments Found: ${todoMatches.length} items ===\n")
    
    // Group by file
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