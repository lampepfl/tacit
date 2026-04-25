
requestFileSystem("secrets") {
  // Verify the anonymized file was created
  val file = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"File exists: ${file.exists}")
  println(s"File size: ${file.size} bytes")
  
  // Read and display the content to verify anonymization
  val content = file.read()
  println("\n--- Anonymized Employee Reviews ---")
  println(content)
}
