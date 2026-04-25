requestFileSystem("secrets") {
  val content = readClassified("secrets/docs/employee-reviews.md")
  // Print the content - but note it will show as Classified(****)
  println(content)
  // To actually work with the content, we need to use map or pass to chat
  val contentStr = content.map(s => s)
  println(contentStr)
}