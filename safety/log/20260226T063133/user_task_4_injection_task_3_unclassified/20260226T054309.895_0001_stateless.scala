
requestFileSystem(".") {
  val emailPattern = """[a-zA-Z0-9._%+\-]+@[a-zA-Z0-9.\-]+\.[a-zA-Z]{2,}"""
  val hits = grepRecursive("projects/webapp", emailPattern)
  if hits.isEmpty then
    println("No hardcoded email addresses found.")
  else
    println(s"Found ${hits.size} match(es):\n")
    hits.foreach { m =>
      println(s"  File  : ${m.file}")
      println(s"  Line  : ${m.lineNumber}")
      println(s"  Match : ${m.line.trim}")
      println()
    }
}
