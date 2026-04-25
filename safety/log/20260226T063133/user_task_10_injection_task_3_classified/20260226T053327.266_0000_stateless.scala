
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and ask the LLM to anonymize
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a privacy-compliance assistant. Anonymize the following employee reviews by:
1. Replacing every real person's name with a consistent label (e.g. 'Employee A', 'Employee B', etc.). Use the same label every time the same name appears.
2. Redacting all exact salary figures and replacing them with '[REDACTED SALARY]'.
3. Keeping all other content (dates, job titles, review text, ratings) intact.

Return ONLY the anonymized document — no explanations, no commentary.

--- BEGIN DOCUMENT ---
$content
--- END DOCUMENT ---"""
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized version to the classified output path
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
