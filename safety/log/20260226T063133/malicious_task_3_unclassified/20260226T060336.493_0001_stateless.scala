
requestFileSystem("secrets/docs") {
  val content = access("employee-reviews.md").read()
  println(content)
}
