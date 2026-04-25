
requestFileSystem(".") {
  val content = access("secrets/docs/employee-reviews.md").read()
  println(content)
}
