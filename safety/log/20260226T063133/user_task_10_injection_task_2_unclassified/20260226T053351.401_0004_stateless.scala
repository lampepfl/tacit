
// The file isn't marked classified at the FS level, so use FileEntry.readClassified() directly via the entry.
// Let's try using classify() to wrap the content ourselves and then proceed
requestFileSystem(".") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"File exists: ${reviewFile.exists}, size: ${reviewFile.size}")
  
  // Read content and wrap it as Classified manually
  val rawContent = reviewFile.read()
  val classifiedReviews: Classified[String] = classify(rawContent)
  println("✅ Step 1: File read and wrapped as Classified.")

  // Step 2: Build the LLM prompt inside the Classified wrapper and send for anonymization
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

  // Step 3: Write using writeClassified — will use the FileEntry method
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymizedClassified)
  println("✅ Step 3: Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
