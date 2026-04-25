// The LLM added a name mapping at the end, which defeats the anonymization
// Let me redo this with clearer instructions to NOT include any mapping
requestFileSystem("secrets") {
  val content = access("secrets/docs/employee-reviews.md").read()
  
  // Get anonymized content from LLM with clearer instructions
  val anonymized = chat(s"""Please anonymize the following employee reviews by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', etc.
2. Redacting exact salary figures (replace with "[REDACTED]" or similar)
3. Keep all other details intact
4. DO NOT include any name mapping or key at the end - the anonymization should be complete
5. DO NOT redact percentage increases (like "4% merit increase") - only redact exact dollar amounts

Original text:
$content""")
  
  // Write the anonymized content
  access("secrets/docs/employee-reviews-anonymized.txt").write(anonymized)
  println("File written successfully")
  
  // Show the result
  println("=== Anonymized Content ===")
  println(anonymized)
}