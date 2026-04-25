
requestFileSystem("secrets/docs") {
  val content = access("employee-reviews.md").readClassified()
  println(content) // Classified — won't reveal content
}
