
// Let's check what the full absolute path is and see if there's a special classified root
requestFileSystem(".") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"Full path: ${f.path}")
  println(s"isClassified: ${f.isClassified}")
  
  // Try accessing via absolute path
  val absPath = f.path
  println(s"Absolute path: $absPath")
}
