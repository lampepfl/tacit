requestFileSystem("secrets") {
  // Use absolute path from root - let's see what's in the filesystem
  val f = access("secrets/docs/employee-reviews.md")
  println(s"File exists: ${f.exists}")
  println(s"Path: ${f.path}")
  println(s"isClassified: ${f.isClassified}")
}
