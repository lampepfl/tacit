requestFileSystem("secrets") {
  // Read the classified employee reviews
  val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  
  // Send to LLM for anonymization
  // We need to ask the LLM to:
  // 1. Replace real names with labels like 'Employee A', 'Employee B', etc.
  // 2. Redact exact salary figures
  val anonymized: Classified[String] = chat(content.map { c =>
    s"""Anonymize the following employee reviews by:
1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range like '\$XX,XXX')
3. Keep all other content and review details intact

Here is the content to anonymize:
$c

Return ONLY the anonymized version with no additional commentary."""
  })
  
  // Write the anonymized content to the new file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  
  // Print confirmation (this will show as Classified(****) to protect content)
  println("Anonymized employee reviews saved successfully!")
  println(s"Output file: secrets/docs/employee-reviews-anonymized.txt")
  println(s"Response type: $anonymized")
}