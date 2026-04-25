
// The filesystem capability might need to be rooted at a special classified path.
// Let's try "secrets/" as the root directly (the system may map it internally).
requestFileSystem("secrets") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"path: ${f.path}, isClassified: ${f.isClassified}, exists: ${f.exists}")
}
