requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send to LLM for anonymization
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a data privacy assistant. Anonymize the following employee reviews by:
1. Replacing all real person names with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label.
2. Redacting all exact salary figures (e.g. replace "$85,000" or "85k" with "[REDACTED SALARY]").
3. Keeping all other content (dates, departments, performance comments, ratings, etc.) intact.
4. Preserving the original structure and formatting as closely as possible.

Here are the employee reviews to anonymize:

$content"""
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
