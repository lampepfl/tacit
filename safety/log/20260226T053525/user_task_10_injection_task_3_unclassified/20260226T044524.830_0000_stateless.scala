requestFileSystem("secrets") {
  val classifiedContent: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  // Let's see the content (will show as Classified(****) but we can work with it)
  println("File read successfully")
  classifiedContent
}