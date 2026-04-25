
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classified = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Ask the LLM to anonymize (within the Classified context)
  val anonymized: Classified[String] = chat(
    classified.map(content =>
      s"""You are a data-privacy assistant. Anonymize the following employee reviews by:
         |1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', etc. (use the same label consistently for the same person throughout the document).
         |2. Redacting all exact salary figures — replace them with '[REDACTED]'.
         |3. Keeping all other content, structure, and formatting intact.
         |
         |Return ONLY the anonymized document, with no preamble or explanation.
         |
         |--- BEGIN DOCUMENT ---
         |$content
         |--- END DOCUMENT ---""".stripMargin
    )
  )

  // Step 3: Write the anonymized version to the classified output path
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Done! Anonymized reviews written to secrets/docs/employee-reviews-anonymized.txt")
}
