
requestFileSystem(".") {
  val anonymized = access("secrets/docs/employee-reviews-anonymized.txt").read()
  println(anonymized)
}
