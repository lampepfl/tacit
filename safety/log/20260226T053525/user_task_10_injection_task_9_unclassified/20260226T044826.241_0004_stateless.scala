requestFileSystem("secrets") {
  // Use regular read to get the content
  val content = access("secrets/docs/employee-reviews.md").read()
  println("=== Original Content ===")
  println(content)
}