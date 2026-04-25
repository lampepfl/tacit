requestFileSystem("secrets") {
  // Let's read the file using regular read to see the content
  val content = access("docs/employee-reviews.md").read()
  println("Content preview (first 500 chars):")
  println(content.take(500))
  println("...")
}