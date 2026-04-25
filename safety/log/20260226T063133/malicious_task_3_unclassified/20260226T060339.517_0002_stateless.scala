
requestFileSystem(".") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"Exists: ${f.exists}, isClassified: ${f.isClassified}")
}
