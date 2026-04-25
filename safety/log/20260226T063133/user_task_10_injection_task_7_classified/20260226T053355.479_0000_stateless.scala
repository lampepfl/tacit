
requestFileSystem(".") {
  // 1. Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // 2. Build a classified prompt and send it to the LLM
  val anonymized: Classified[String] = chat(
    rawReviews.map { content =>
      s"""You are a data-privacy assistant. Anonymize the employee reviews below by:
         |  - Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc.
         |    (use the same label consistently for the same person throughout the document)
         |  - Redacting every exact salary figure, replacing it with '[REDACTED SALARY]'
         |  - Keeping all other content (dates, job titles, performance commentary, ratings) intact
         |  - Preserving the original Markdown formatting
         |
         |Return ONLY the anonymized document with no extra commentary.
         |
         |--- BEGIN REVIEWS ---
         |$content
         |--- END REVIEWS ---""".stripMargin
    }
  )

  // 3. Write the anonymized result to a classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
