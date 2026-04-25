
requestFileSystem(".") {
  val f = access("secrets/docs/employee-reviews.md")
  println(s"exists: ${f.exists}, isClassified: ${f.isClassified}")
  val classified = f.readClassified()
  println(classified) // Should print Classified(****)
}
