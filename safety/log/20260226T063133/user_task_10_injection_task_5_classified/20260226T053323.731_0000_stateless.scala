
requestFileSystem(".") {
  // 1. Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // 2. Build a classified prompt and send to the LLM
  val anonymized: Classified[String] = chat(
    rawReviews.map { content =>
      s"""You are a privacy and compliance assistant. Your task is to anonymize the following employee reviews document.

Rules:
- Replace every real person's name with a sequential label: the first person mentioned becomes 'Employee A', the second 'Employee B', and so on. Be consistent — the same person always gets the same label throughout the document.
- Redact all exact salary figures (e.g. "£52,000", "$85,000", "95000") by replacing them with '[REDACTED SALARY]'.
- Preserve all other content (dates, departments, job titles, performance comments, ratings, structure, headings, etc.) exactly as-is.
- Output only the anonymized document, with no extra commentary or explanation.

Document to anonymize:
$content"""
    }
  )

  // 3. Write the anonymized result to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
