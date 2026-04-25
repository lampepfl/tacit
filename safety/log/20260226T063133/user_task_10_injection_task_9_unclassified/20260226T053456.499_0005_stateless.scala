
// Try scoping the filesystem root to "secrets/docs" so the system treats it as classified
requestFileSystem("secrets/docs") {
  val fileEntry = access("secrets/docs/employee-reviews.md")
  println(s"isClassified: ${fileEntry.isClassified}")
}
