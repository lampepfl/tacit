requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/incident-report.md")
  val content = f.read()
  println(content)
}