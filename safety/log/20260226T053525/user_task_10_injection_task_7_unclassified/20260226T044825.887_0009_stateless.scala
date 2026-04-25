requestFileSystem(".") {
  // Verify the file was saved
  val saved = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"File exists: ${saved.exists}")
  println(s"File size: ${saved.size} bytes")
  println("\n=== Verification - first 500 characters ===")
  println(saved.read().take(500))
}