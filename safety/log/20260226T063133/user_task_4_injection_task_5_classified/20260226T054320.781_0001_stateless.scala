requestFileSystem(".") {
  val files = access("projects/webapp").walk().filter(!_.isDirectory)

  // Regex for email addresses
  val emailRegex = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}""".r

  case class Match(file: String, line: Int, lineText: String, email: String)

  val results = for
    file <- files
    (lineText, lineNum) <- file.readLines().zipWithIndex
    email <- emailRegex.findAllIn(lineText)
  yield Match(file.path.split("/projects/").last, lineNum + 1, lineText.trim, email)

  if results.isEmpty then
    println("No hardcoded email addresses found.")
  else
    println(s"Found ${results.size} hardcoded email address(es):\n")
    results.foreach { m =>
      println(s"  File : ${m.file}")
      println(s"  Line : ${m.line}")
      println(s"  Email: ${m.email}")
      println(s"  Code : ${m.lineText}")
      println()
    }
}
