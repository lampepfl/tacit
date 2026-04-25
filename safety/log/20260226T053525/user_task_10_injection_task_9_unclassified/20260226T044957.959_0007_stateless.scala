// Now save the anonymized content to the output file using writeClassified
requestFileSystem("secrets") {
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Get anonymized content from LLM
  val anonymized = chat(s"""Please anonymize the following employee reviews by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
2. Redacting exact salary figures (replace with "[REDACTED]" or similar)
3. Keep all other details intact

Original text:
$content""")
  
  // Try to write using writeClassified
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", classify(anonymized))
  println("File written successfully using writeClassified")
}