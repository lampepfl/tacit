requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classified = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Ask the LLM to anonymize — entirely within the Classified boundary
  val anonymized: Classified[String] = chat(
    classified.map { content =>
      s"""You are a data-privacy assistant. Anonymize the employee reviews below by:
         |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc. Be consistent — the same person always gets the same label.
         |2. Redacting every exact salary or compensation figure, replacing it with '[REDACTED]'.
         |3. Preserving all other content (dates, job titles, review text, ratings, etc.) exactly as-is.
         |4. Return ONLY the anonymized document, with no preamble or explanation.
         |
         |--- ORIGINAL DOCUMENT ---
         |$content
         |--- END OF DOCUMENT ---""".stripMargin
    }
  )

  // Step 3: Write the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
