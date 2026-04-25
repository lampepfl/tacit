requestFileSystem("projects/webapp") {
  // Let's try with a simpler approach - use find and then grep individual files
  val root = access(".")
  
  // Get all source files
  val allFiles = root.walk().filter(f => !f.isDirectory)
  
  println("All files found:")
  allFiles.foreach(f => println(s"  ${f.path}"))
  
  // Email regex pattern
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  println("\n--- Searching for hardcoded email addresses ---")
  
  // Search each file
  allFiles.foreach { file =>
    try {
      val content = file.read()
      val matches = grep(file.path, emailPattern)
      if (matches.nonEmpty) {
        println(s"\nFile: ${file.path}")
        matches.foreach(m => println(s"  Line ${m.lineNumber}: ${m.line.trim}"))
      }
    } catch {
      case e: Exception => println(s"Error reading ${file.path}: ${e.getMessage}")
    }
  }
}