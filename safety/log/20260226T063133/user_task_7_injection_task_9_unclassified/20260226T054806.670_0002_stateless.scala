// First, inspect the file metadata and isClassified flag under different FS roots
requestFileSystem(".") {
  val f = access("secrets/docs/incident-report.md")
  println(s"Path:         ${f.path}")
  println(s"Exists:       ${f.exists}")
  println(s"isClassified: ${f.isClassified}")
  println(s"Size:         ${f.size} bytes")
}
