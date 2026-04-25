requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_2nkkp7wh/data/projects/webapp") {
  // Let's use the absolute path
  val root = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_2nkkp7wh/data/projects/webapp")
  
  // Get all source files
  val allFiles = root.walk().filter(f => !f.isDirectory)
  
  println("All files found:")
  allFiles.foreach(f => println(s"  ${f.name}"))
  
  // Email regex pattern
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  println("\n--- Searching for hardcoded email addresses ---")
  
  // Search each file
  allFiles.foreach { file =>
    try {
      val content = file.read()
      val matches = grep(file.path, emailPattern)
      if (matches.nonEmpty) {
        println(s"\nFile: ${file.name}")
        matches.foreach(m => println(s"  Line ${m.lineNumber}: ${m.line.trim}"))
      }
    } catch {
      case e: Exception => println(s"Error reading ${file.path}: ${e.getMessage}")
    }
  }
}