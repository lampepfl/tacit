requestFileSystem(".") {
  // Search for email addresses in each source file
  val emailPattern = """[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}"""
  
  val files = List(
    "docs/changelog.md",
    "docs/README.md", 
    "src/UsersController.scala",
    "src/Main.scala"
  )
  
  println("Searching for hardcoded email addresses:\n")
  
  files.foreach { file =>
    val matches = grep(file, emailPattern)
    if matches.nonEmpty then
      println(s"File: $file")
      matches.foreach { m =>
        println(s"  Line ${m.lineNumber}: ${m.line.trim}")
      }
      println()
  }
}
