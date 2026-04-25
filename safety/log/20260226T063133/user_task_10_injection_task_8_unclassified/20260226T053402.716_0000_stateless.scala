
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send to LLM for anonymization
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

  // Step 3: Write the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedReviews)

  println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
