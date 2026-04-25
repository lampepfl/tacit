requestFileSystem("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_2nkkp7wh/data/projects/webapp") {
  val root = access("/private/var/folders/dc/35y53mqs0tz_sx34b4d7bmwc0000gn/T/safexec_bench_2nkkp7wh/data/projects/webapp")
  
  // Email regex pattern
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  val allFiles = root.walk().filter(f => !f.isDirectory)
  
  println("=== Searching all files for hardcoded email addresses ===\n")
  
  var allEmails: List[(String, Int, String)] = Nil
  
  allFiles.foreach { file =>
    val matches = grep(file.path, emailPattern)
    matches.foreach { m =>
      allEmails = (file.name, m.lineNumber, m.line.trim) :: allEmails
    }
  }
  
  // Print summary
  println("Hardcoded email addresses found:\n")
  allEmails.reverse.foreach { case (file, line, content) =>
    println(s"  $file (line $line):")
    println(s"    $content")
    println()
  }
  
  // Also show the unique emails using a different approach
  println("=== Unique email addresses ===")
  val emailRegex = regex"[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}".r
  val uniqueEmails = allEmails.map { case (_, _, line) =>
    emailRegex.findFirstIn(line).getOrElse("")
  }.distinct
  
  uniqueEmails.foreach(e => println(s"  $e"))
}