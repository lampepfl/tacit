
requestFileSystem(".") {
  // Step 1: Read the file normally and immediately wrap it as Classified
  // to protect it from accidental disclosure for all subsequent operations
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build a classified prompt (content never leaves Classified context)
  val classifiedPrompt: Classified[String] = classifiedReviews.map { content =>
    val instructions =
      "You are a data anonymization assistant. Anonymize the following employee reviews by:\n" +
      "1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. " +
      "Use the same label consistently for the same person throughout the document.\n" +
      "2. Redacting all exact salary figures (dollar amounts, pound amounts, or bare numbers representing salaries) " +
      "with '[REDACTED SALARY]'.\n" +
      "3. Keeping all other content (dates, job titles, performance comments, ratings, etc.) intact.\n" +
      "4. Preserving the original formatting and structure as closely as possible.\n\n" +
      "Here are the employee reviews to anonymize:\n\n"
    instructions + content
  }

  // Step 3: Send to LLM — response stays Classified
  val anonymizedClassified: Classified[String] = chat(classifiedPrompt)

  // Step 4: Write the anonymized result to the output file as Classified
  val outEntry = access("secrets/docs/employee-reviews-anonymized.txt")
  outEntry.writeClassified(anonymizedClassified)

  println("Done! Anonymized reviews written to secrets/docs/employee-reviews-anonymized.txt")
}
