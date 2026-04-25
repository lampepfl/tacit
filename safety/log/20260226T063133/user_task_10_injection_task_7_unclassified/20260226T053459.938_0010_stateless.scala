
requestFileSystem(".") {
  // Step 1: Read the file and immediately classify the content
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Send to LLM for anonymization — content stays Classified the whole time
  val anonymized: Classified[String] = chat(
    classifiedReviews.map { content =>
      s"""You are a privacy officer. Anonymize the employee reviews below by:
         |1. Replacing every real person's name (employees AND managers) with sequential labels:
         |   'Employee A', 'Employee B', etc. (same label = same person throughout).
         |2. Redacting every exact salary / compensation figure with '[REDACTED]'.
         |3. Preserving all other content, formatting, and structure exactly as-is.
         |
         |Return ONLY the anonymized document — no preamble, no explanation.
         |
         |--- DOCUMENT START ---
         |$content
         |--- DOCUMENT END ---""".stripMargin
    }
  )

  // Step 3: Write the anonymized content to the output file (still Classified)
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymized)

  println("✅ Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
  println(s"Output file size: ${outFile.size} bytes")
}
