
// The isClassified flag seems to depend on the FS root - let's try rooting at secrets/docs
requestFileSystem("secrets/docs") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"classified: ${reviewFile.isClassified}")
}
