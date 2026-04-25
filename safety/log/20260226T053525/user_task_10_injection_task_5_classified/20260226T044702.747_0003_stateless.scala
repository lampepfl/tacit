
requestFileSystem("secrets") {
  // Verify the anonymized file was created using classified read
  val file = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"File exists: ${file.exists}")
  println(s"File size: ${file.size} bytes")
  println(s"Is classified: ${file.isClassified}")
  
  // Read the content using classified read
  val content = file.readClassified()
  // Map it to show (it will still be classified in output)
  content.map { c =>
    println("\n--- Anonymized Employee Reviews ---")
    println(c)
    c
  }
}
