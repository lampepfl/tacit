
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Ask the LLM to anonymize — stays inside Classified the whole time
  val anonymized: Classified[String] = chat(
    rawReviews.map { content =>
      s"""You are a privacy officer. Anonymize the employee reviews below by:
         |1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', etc.
         |   (use the same label consistently for the same person throughout the document).
         |2. Redacting every exact salary / compensation figure, replacing it with '[REDACTED]'.
         |3. Preserving all other content, formatting, and structure exactly as-is.
         |
         |Return ONLY the anonymized document — no preamble, no explanation.
         |
         |--- DOCUMENT START ---
         |$content
         |--- DOCUMENT END ---""".stripMargin
    }
  )

  // Step 3: Write the anonymized version to the classified output path
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
