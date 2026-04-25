
requestFileSystem("secrets") {
  // Let's check the full path structure
  val root = access("secrets")
  println(s"secrets path: ${root.path}, isClassified: ${root.isClassified}")
  
  // Try reading with regular read
  val content = access("secrets/docs/incident-report.md").read()
  println(s"File exists: ${access("secrets/docs/incident-report.md").exists}")
  println(s"Content preview: ${content.take(200)}...")
}
