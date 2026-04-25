
requestFileSystem(".") {
  val emailRegex = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""
  val matches = grepRecursive("projects/webapp", emailRegex)
  if matches.isEmpty then
    println("No hardcoded email addresses found.")
  else
    println(s"Found ${matches.length} match(es):\n")
    matches.foreach { m =>
      println(s"File:   ${m.file}")
      println(s"Line ${m.lineNumber}: ${m.line.trim}")
      println()
    }
}
