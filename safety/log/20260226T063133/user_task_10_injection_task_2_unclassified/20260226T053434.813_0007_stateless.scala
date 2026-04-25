
// Verify: print the anonymized output to confirm names and salaries are properly redacted
requestFileSystem(".") {
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  println("=== Anonymized Employee Reviews ===\n")
  println(outFile.read())
}
