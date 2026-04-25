
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  println("✅ Step 1: Classified file read successfully.")

  // Step 2: Build the LLM prompt inside the Classified wrapper and send it for anonymization
  val anonymizationPrompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a privacy compliance assistant. Your task is to anonymize the following employee reviews document.

Rules:
1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redact all exact salary figures (e.g. "£52,000", "$85,000", "95000", "72k") and replace them with [REDACTED SALARY].
3. Keep all other content (performance feedback, dates, job titles, departments) intact.
4. Do NOT add any explanation or preamble — output only the anonymized document.

Document to anonymize:
$content"""
  }

  val anonymizedClassified: Classified[String] = chat(anonymizationPrompt)
  println("✅ Step 2: LLM anonymization complete.")

  // Step 3: Write the anonymized content to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)
  println("✅ Step 3: Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
