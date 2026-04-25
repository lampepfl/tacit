requestFileSystem("secrets/docs") {
  // Try with relative path inside the classified directory
  val content = readClassified("employee-reviews.md")
  println("Successfully read the file")
}
