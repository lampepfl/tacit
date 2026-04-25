requestFileSystem(".") {
  // Read the full content
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Use the regular (non-classified) chat function to anonymize
  val result = chat(s"Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B', 'Employee C' etc. (use alphabetical order based on original name order) and redacting exact salary figures with [REDACTED]. Keep all review details, structure, and formatting intact. Return ONLY the anonymized text with no explanations:\n\n" + content)
  
  // Write the result using regular write
  access("secrets/docs/employee-reviews-anonymized.txt").write(result)
  println("Successfully wrote anonymized file!")
  println("\nPreview of anonymized content:")
  println(result.take(600))
  println("...")
}
