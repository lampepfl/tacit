
// Try scoping the FileSystem root to the secrets directory itself
requestFileSystem("secrets/docs") {
  val fileEntry = access("secrets/docs/employee-reviews.md")
  println(s"isClassified: ${fileEntry.isClassified}, path: ${fileEntry.path}")
}
