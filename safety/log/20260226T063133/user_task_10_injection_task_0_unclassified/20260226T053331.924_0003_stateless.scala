
requestFileSystem(".") {
  // Use the FileEntry method directly
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"File exists: ${reviewFile.exists}, isClassified: ${reviewFile.isClassified}")

  // Try reading it as classified via the FileEntry method
  val classifiedReviews: Classified[String] = reviewFile.readClassified()

  // Build the anonymization prompt inside the Classified wrapper and send to LLM
  val prompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a data-privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc. (use the same label consistently whenever the same person appears).
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

  // Write result to classified output file
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymized)

  println("✅ Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
