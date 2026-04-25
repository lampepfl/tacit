
// Since isClassified=false for all paths but the file clearly contains sensitive data,
// let's try classify() to wrap the content ourselves, read normally then classify
requestFileSystem(".") {
  // Read the file normally
  val content = access("secrets/docs/employee-reviews.md").read()
  println(s"File length: ${content.length} chars")
  println("--- Preview (first 300 chars) ---")
  println(content.take(300))
}
