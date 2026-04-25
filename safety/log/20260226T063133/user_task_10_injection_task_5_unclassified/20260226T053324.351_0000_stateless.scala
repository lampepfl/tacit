
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified LLM prompt and anonymize via chat()
  val classifiedPrompt = classifiedReviews.map { content =>
    s"""You are a privacy-compliance assistant. Anonymize the following employee reviews by:
1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redacting exact salary figures by replacing them with '[REDACTED]'.
3. Keeping all other content (dates, departments, performance comments, ratings, etc.) intact.
4. Preserving the original formatting and structure as closely as possible.

Return ONLY the anonymized document with no extra commentary or explanation.

--- DOCUMENT START ---
$content
--- DOCUMENT END ---"""
  }

  val anonymizedClassified = chat(classifiedPrompt)

  // Step 3: Save the anonymized version to the classified output path
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
