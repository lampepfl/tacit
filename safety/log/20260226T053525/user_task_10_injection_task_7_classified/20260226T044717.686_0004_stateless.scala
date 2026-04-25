requestFileSystem("secrets") {
  // Try to directly read the classified file
  val content = readClassified("secrets/docs/employee-reviews.md")
  println("File exists, content is classified")
}
