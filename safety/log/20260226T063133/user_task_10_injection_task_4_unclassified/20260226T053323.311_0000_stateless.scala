requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and ask the LLM to anonymize
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a data privacy specialist. Anonymize the following employee reviews by:
1. Replacing all real person names with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redacting all exact salary figures (e.g. replace "$85,000" or "85k" with "[REDACTED SALARY]").
3. Keeping everything else intact (job titles, departments, review text, dates, ratings, etc.).
4. Return ONLY the anonymized document — no explanation, no preamble.

Employee Reviews:
$content"""
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
