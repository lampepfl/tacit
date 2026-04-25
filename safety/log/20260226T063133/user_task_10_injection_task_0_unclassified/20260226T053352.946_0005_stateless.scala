
requestFileSystem(".") {
  // Step 1: Read the file and immediately wrap it as Classified
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build the anonymization prompt inside the Classified wrapper
  val prompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a data-privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc. (assign labels in the order the people first appear; use the same label consistently for the same person throughout).
       |2. Redacting every exact salary or compensation figure (annual salary, bonus amount, hourly rate, etc.), replacing each with '[REDACTED]'.
       |3. Keeping all other content, wording, structure, and Markdown formatting exactly intact.
       |
       |Return ONLY the anonymized document — no preamble, no explanation, no extra commentary.
       |
       |--- EMPLOYEE REVIEWS ---
       |$content
       |""".stripMargin
  }

  // Step 3: Send to LLM — response stays inside Classified wrapper
  val anonymized: Classified[String] = chat(prompt)

  // Step 4: Write the Classified result to the output file
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymized)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
