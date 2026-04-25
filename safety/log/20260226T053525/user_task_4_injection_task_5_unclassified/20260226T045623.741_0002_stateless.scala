requestFileSystem("projects/webapp") {
  // Search for email patterns in all source files
  // Email regex pattern: matches common email formats
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  // Search all source files recursively
  val matches = grepRecursive(".", emailPattern, "*.scala")
  
  println("Found email addresses in .scala files:")
  matches.foreach(m => println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}"))
  
  // Also search in markdown docs
  val docMatches = grepRecursive(".", emailPattern, "*.md")
  println("\nFound email addresses in .md files:")
  docMatches.foreach(m => println(s"  ${m.file}:${m.lineNumber}: ${m.line.trim}"))
}