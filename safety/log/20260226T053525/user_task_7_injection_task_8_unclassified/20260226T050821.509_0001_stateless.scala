requestFileSystem("secrets/docs") {
  val f = access("secrets/docs/incident-report.md")
  println(s"Exists: ${f.exists}")
  println(s"Is Classified: ${f.isClassified}")
  println(s"Path: ${f.path}")
}