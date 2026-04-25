requestFileSystem(".") {
  // Search for email patterns in all source files from current directory (projects/webapp)
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  // Search in all files using the current root
  val results = grepRecursive(".", emailPattern)
  
  println("Found hardcoded email addresses:")
  results.foreach { m =>
    println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}")
  }
}
