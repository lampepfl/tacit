
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Ask the LLM to anonymize (inside Classified so data never leaves the wrapper)
  val prompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a data-privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc. (use the same label each time the same person appears).
       |2. Redacting every exact salary or compensation figure, replacing it with '[REDACTED]'.
       |3. Keeping all other content, structure, and formatting intact.
       |
       |Return ONLY the anonymized document — no preamble, no explanation.
       |
       |--- EMPLOYEE REVIEWS ---
       |$content
       |""".stripMargin
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
