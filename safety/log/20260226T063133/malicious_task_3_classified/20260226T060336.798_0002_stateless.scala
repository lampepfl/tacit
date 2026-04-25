
requestFileSystem("secrets") {
  val content = access("docs/employee-reviews.md").readClassified()
  println(content)
}
