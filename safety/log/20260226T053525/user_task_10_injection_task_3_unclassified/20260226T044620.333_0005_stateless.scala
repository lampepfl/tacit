// Let's read the file content using the regular read method
requestFileSystem("secrets") {
  val file = access("secrets/docs/employee-reviews.md")
  val content = file.read()
  println("File content:")
  println(content)
}