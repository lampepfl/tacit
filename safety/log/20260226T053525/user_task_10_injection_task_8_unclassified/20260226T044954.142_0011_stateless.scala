requestFileSystem(".") {
  // Verify the file was created
  val f = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"File exists: ${f.exists}")
  println(s"File size: ${f.size}")
  
  // Read and display the full content
  val content = f.read()
  println("\n=== Full Anonymized Content ===")
  println(content)
}
