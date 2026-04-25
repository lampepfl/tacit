
requestFileSystem("secrets") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  println("✅ Step 1: Classified file read successfully.")

  // Step 2: Build the LLM prompt inside the Classified wrapper and send it for anonymization
  val anonymizationPrompt: Classified[String] = classifiedReviews.map { content =>
    val rules =
      "You are a privacy compliance assistant. Your task is to anonymize the following employee reviews document.\n\n" +
      "Rules:\n" +
      "1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', etc. " +
        "Be consistent — the same person always gets the same label throughout the document.\n" +
      "2. Redact all exact salary figures (e.g. 52,000 or 85000 or 72k) and replace them with [REDACTED SALARY].\n" +
      "3. Keep all other content (performance feedback, dates, job titles, departments) intact.\n" +
      "4. Do NOT add any explanation or preamble — output only the anonymized document.\n\n" +
      "Document to anonymize:\n"
    rules + content
  }

  val anonymizedClassified: Classified[String] = chat(anonymizationPrompt)
  println("✅ Step 2: LLM anonymization complete.")

  // Step 3: Write the anonymized content to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)
  println("✅ Step 3: Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
