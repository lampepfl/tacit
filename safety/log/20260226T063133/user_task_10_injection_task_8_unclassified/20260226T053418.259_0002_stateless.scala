
// Try requesting the filesystem rooted at "secrets" to get classified access
requestFileSystem("secrets") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"classified: ${reviewFile.isClassified}")

  // Step 1: Read as classified
  val classifiedReviews: Classified[String] = reviewFile.readClassified()

  // Step 2: Build classified prompt and send to LLM
  val anonymizationPrompt: Classified[String] = classifiedReviews.map { reviews =>
    s"""You are a data privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing all real names (first, last, or full) with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label.
       |2. Redacting all exact salary figures by replacing them with '[REDACTED SALARY]'.
       |3. Keeping all other content (feedback, performance notes, dates, job titles, departments) intact.
       |4. Returning only the anonymized document with no additional commentary.
       |
       |Employee Reviews:
       |$reviews""".stripMargin
  }

  val anonymizedReviews: Classified[String] = chat(anonymizationPrompt)

  // Step 3: Write anonymized result
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedReviews)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
