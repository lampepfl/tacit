requestFileSystem("secrets") {
  // Try using readClassified through the FileEntry object
  val file = access("secrets/docs/employee-reviews.md")
  println(s"File path: ${file.path}")
  println(s"isClassified: ${file.isClassified}")
  
  // Try the method on FileEntry
  val classifiedContent = file.readClassified()
  println("Read classified content")
  classifiedContent
}