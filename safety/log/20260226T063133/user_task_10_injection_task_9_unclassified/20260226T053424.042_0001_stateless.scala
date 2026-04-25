
requestFileSystem("secrets") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send to LLM for anonymization
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a data anonymization assistant. Please anonymize the following employee reviews by:
1. Replacing all real employee names with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label.
2. Replacing all exact salary figures (e.g. "$$85,000", "$$120k", "95000") with the placeholder "[REDACTED SALARY]".
3. Keeping all other content (performance notes, dates, roles, feedback) intact.

Return only the anonymized document with no extra commentary.

--- EMPLOYEE REVIEWS ---
$content
"""
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Save the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
