requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Ask the LLM to anonymize the content (inside Classified context)
  val anonymizedReviews: Classified[String] = chat(
    classifiedReviews.map(reviews =>
      "You are a data privacy specialist. Anonymize the following employee reviews by:\n" +
      "1. Replacing all real person names with sequential labels: 'Employee A', 'Employee B', etc.\n" +
      "   (use the same label consistently for the same person throughout).\n" +
      "2. Redacting all exact salary figures (e.g. replace amounts like 85,000 or 85k with [REDACTED SALARY]).\n" +
      "3. Keeping all other content (job titles, departments, review text, dates, ratings) intact.\n" +
      "4. Returning ONLY the anonymized document with no extra commentary.\n\n" +
      "Employee reviews to anonymize:\n" +
      reviews
    )
  )

  // Step 3: Write the anonymized content to the output classified file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedReviews)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
