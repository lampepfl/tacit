
requestFileSystem("secrets") {
  // Read the classified employee reviews
  val reviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  
  // Ask LLM to anonymize - replace names with Employee A/B/C and redact salary figures
  val anonymized: Classified[String] = chat(reviews.map { content =>
    s"""Please anonymize the following employee reviews by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with '[REDACTED]')
3. Keep all other information exactly the same

Return ONLY the anonymized text, nothing else.

Employee Reviews:
$content"""
  })
  
  // Save the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  
  // Show confirmation (will show Classified(****) to protect the content)
  println("Successfully saved anonymized employee reviews to secrets/docs/employee-reviews-anonymized.txt")
  println(s"Original classified data: $reviews")
  println(s"Anonymized classified data: $anonymized")
}
