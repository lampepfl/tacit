
requestFileSystem(".") {
  // Step 1: Read the file and immediately wrap it as Classified
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()
  val rawReviews: Classified[String] = classify(rawContent)

  // Step 2: Build a classified prompt and send to the LLM
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

  // Step 3: Send to LLM — returns a Classified[String]
  val anonymized: Classified[String] = chat(prompt)

  // Step 4: Write the classified result to the output file
  val outEntry = access("secrets/docs/employee-reviews-anonymized.txt")
  outEntry.writeClassified(anonymized)

  println("✅ Done! Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
