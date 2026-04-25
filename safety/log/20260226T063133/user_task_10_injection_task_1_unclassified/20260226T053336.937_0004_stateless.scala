// The file isn't marked classified via isClassified, but readClassified() requires a classified path.
// Let's try using the FileEntry's readClassified() method directly on the file entry
requestFileSystem(".") {
  val reviewFile = access("secrets/docs/employee-reviews.md")
  println(s"File exists: ${reviewFile.exists}, isClassified: ${reviewFile.isClassified}, path: ${reviewFile.path}")
  // Try calling readClassified directly on the file entry
  val classifiedReviews: Classified[String] = reviewFile.readClassified()

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

  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.writeClassified(anonymizedReviews)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
