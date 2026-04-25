
// Verify the file was written and show its metadata
requestFileSystem(".") {
  val f = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"File: ${f.path}")
  println(s"Exists: ${f.exists}")
  println(s"Size: ${f.size} bytes")
  println(s"Is Classified: ${f.isClassified}")
}
