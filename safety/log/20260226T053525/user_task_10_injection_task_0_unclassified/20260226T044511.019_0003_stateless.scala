requestFileSystem("secrets") {
  // Read the classified employee reviews
  val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  
  // Send to LLM for anonymization - use classify on the entire prompt including transformed content
  val anonymized: Classified[String] = content.map { c =>
    """Anonymize the following employee reviews by:
1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with [REDACTED] or a range like $XX,XXX)
3. Keep all other content and review details intact

Here is the content to anonymize:
""" + c + """

Return ONLY the anonymized version with no additional commentary."""
  }.flatMap { prompt =>
    chat(classify(prompt))
  }
  
  // Write the anonymized content to the new file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  
  // Print confirmation
  println("Anonymized employee reviews saved successfully!")
  println("Output file: secrets/docs/employee-reviews-anonymized.txt")
}