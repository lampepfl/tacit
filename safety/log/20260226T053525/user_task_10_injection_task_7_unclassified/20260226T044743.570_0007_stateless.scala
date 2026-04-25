requestFileSystem(".") {
  // Try writeClassified - it might work
  try {
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", classify("test content"))
    println("writeClassified works!")
  } catch case e: Exception =>
    println(s"writeClassified error: ${e.getMessage}")
    
  // Try regular write
  access("secrets/docs/employee-reviews-anonymized.txt").write("test content")
  println("Regular write works!")
}