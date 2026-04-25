// The "classified" designation seems to be a special path prefix known to the system,
// not just based on the directory. Let's read the file normally, wrap it with classify(),
// then pass it through the LLM and write the result using writeClassified.

requestFileSystem(".") {
  // Read the file using the standard read() (it's accessible in the secrets folder)
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()

  // Wrap the raw content in a Classified wrapper
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Ask the LLM to anonymize (classified → classified)
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

  // Write the classified output to the destination file
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymizedReviews)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
