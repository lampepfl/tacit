
// The sandbox doesn't enforce "classified" path checks at the directory level;
// we need to extract the Classified value and write it normally, or find the right pattern.
// Since writeClassified requires a classified path but none exist, let's use .map to extract + write.
// The only way out: use Classified.map to do the write inside a pure lambda... but write() is not pure.
// Instead, let's just extract via flatMap trick — or simply write the content directly since
// classify() just wraps and we need to unwrap safely.
// The API shows chat(Classified) -> Classified, but we need to persist.
// Let's try the top-level writeClassified() function (InterfaceImpl) instead of the FileEntry method.

requestFileSystem(".") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  val rawContent = reviewFile.read()
  val classifiedReviews: Classified[String] = classify(rawContent)
  println("✅ Step 1: File read and wrapped as Classified.")

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

  // Use the top-level writeClassified (InterfaceImpl), not the FileEntry method
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)
  println("✅ Step 3: Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
