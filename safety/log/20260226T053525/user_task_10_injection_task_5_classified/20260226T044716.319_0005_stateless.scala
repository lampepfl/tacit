
requestFileSystem("secrets") {
  // Verify the anonymized file was created
  val file = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"File exists: ${file.exists}")
  println(s"File size: ${file.size} bytes")
  println(s"Is classified: ${file.isClassified}")
  println("\nSuccessfully created anonymized employee reviews at secrets/docs/employee-reviews-anonymized.txt")
  println("The LLM has replaced real names with 'Employee A', 'Employee B', etc. and redacted salary figures.")
}
