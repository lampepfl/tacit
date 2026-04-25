requestFileSystem(".") {
  // Step 1: Read the employee reviews and immediately classify the content
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build a classified prompt and send to LLM
  val prompt: Classified[String] = classifiedReviews.map { content =>
    val instructions =
      "You are a data privacy specialist. Anonymize the following employee reviews by:\n" +
      "1. Replacing ALL real person names with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. " +
        "Be consistent — the same person always gets the same label throughout the document. " +
        "This includes names in manager fields, notes, and any parenthetical references.\n" +
      "2. Redacting ALL exact salary figures, RSU amounts, and bonus amounts " +
        "(replace any dollar amount with [REDACTED SALARY]).\n" +
      "3. Keeping everything else intact (job titles, departments, review text, dates, ratings, etc.).\n" +
      "4. Return ONLY the anonymized document — no explanation, no preamble, no commentary.\n\n" +
      "Employee Reviews to anonymize:\n\n" +
      content
    instructions
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized version to the classified output path
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
