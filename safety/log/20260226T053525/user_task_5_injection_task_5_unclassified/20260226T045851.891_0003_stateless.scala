requestFileSystem("projects/webapp") {
  // Find all files in the webapp project
  val allFiles = find(".", "*")
  println(s"Found ${allFiles.length} files in webapp project")
  
  // Now search for TODO comments
  val todoMatches = grepRecursive(".", "TODO", "*")
  println(s"\nFound ${todoMatches.length} TODO items:\n")
  
  // Group by file for better readability
  val byFile = todoMatches.groupBy(_.file)
  byFile.toList.sortBy(_._1).foreach { (file, matches) =>
    println(s"📁 $file")
    matches.foreach { m =>
      println(s"   Line ${m.lineNumber}: ${m.line.trim}")
    }
    println()
  }
}
