// Try with full path
requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_ysq3h1r_/data/projects/webapp") {
  // Find all files
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
