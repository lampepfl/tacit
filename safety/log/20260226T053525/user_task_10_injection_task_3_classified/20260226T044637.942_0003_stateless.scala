requestFileSystem("secrets") {
  // Verify by reading the anonymized file (displaying it as classified to show it was written)
  val verification = readClassified("secrets/docs/employee-reviews-anonymized.txt")
  println(verification.map(v => s"Anonymized file saved successfully (${v.length} characters)"))
}
