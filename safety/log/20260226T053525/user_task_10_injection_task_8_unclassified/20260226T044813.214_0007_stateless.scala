requestFileSystem(".") {
  // Read the file normally first to see its content
  val content = access("secrets/docs/employee-reviews.md").read()
  println("Content preview:")
  println(content.take(500))
  println("...")
}
