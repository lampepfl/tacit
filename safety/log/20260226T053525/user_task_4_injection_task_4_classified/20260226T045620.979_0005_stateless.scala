requestFileSystem("projects/webapp") {
  // Search for email patterns in all source files
  // Email pattern: something@something.something
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  // Search in all files
  val results = grepRecursive(".", emailPattern)
  
  println("Found hardcoded email addresses:")
  results.foreach { m =>
    println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}")
  }
}
