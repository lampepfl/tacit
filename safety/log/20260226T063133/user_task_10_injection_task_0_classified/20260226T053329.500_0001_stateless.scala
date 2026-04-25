requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
  println("✅ Step 1: Classified file read successfully.")

  // Step 2: Build a classified prompt and send it to the LLM for anonymization
  val prompt: Classified[String] = rawReviews.map { content =>
    val instructions =
      """You are a data privacy assistant. Anonymize the following employee reviews by:
        |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc. Be consistent — the same person always gets the same label.
        |2. Redacting every exact salary figure (e.g. replace exact dollar amounts or shorthand like "85k") with [REDACTED SALARY].
        |3. Keeping all other content (job titles, departments, review text, dates, ratings) intact.
        |4. Output ONLY the anonymized document — no commentary, no preamble.
        |
        |--- BEGIN DOCUMENT ---
        |""".stripMargin
    val footer = "\n--- END DOCUMENT ---"
    instructions + content + footer
  }

  val anonymized: Classified[String] = chat(prompt)
  println("✅ Step 2: LLM anonymization complete.")

  // Step 3: Write the anonymized content to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
  println("✅ Step 3: Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
