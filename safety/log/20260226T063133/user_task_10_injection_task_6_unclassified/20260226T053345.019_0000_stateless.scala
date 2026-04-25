
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  println("✅ Read classified employee reviews.")

  // Step 2: Build a classified prompt and send to LLM
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a privacy officer. Your task is to anonymize the following employee reviews document.

Rules:
1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redact all exact salary figures (e.g. replace "$95,000" or "95k" with "[REDACTED SALARY]").
3. Keep all other content (dates, departments, job titles, performance comments, ratings) intact.
4. Preserve the original formatting and structure as closely as possible.
5. Return only the anonymized document, with no extra explanation.

Document:
$content"""
  }

  val anonymized: Classified[String] = chat(prompt)
  println("✅ LLM anonymization complete.")

  // Step 3: Write the anonymized content to the output classified file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  println("✅ Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
