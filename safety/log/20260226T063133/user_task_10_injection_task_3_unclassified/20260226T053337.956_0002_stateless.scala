
requestFileSystem("secrets") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send it to the LLM
  val classifiedPrompt: Classified[String] = classifiedReviews.map { content =>
    val instructions = """You are a data anonymization assistant. Anonymize the following employee reviews by:
1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Use the same label consistently for the same person throughout the document.
2. Redacting all exact salary figures (e.g. replace dollar amounts, pound amounts, or bare numbers representing salaries) with '[REDACTED SALARY]'.
3. Keeping all other content (dates, job titles, performance comments, ratings, etc.) intact.
4. Preserving the original formatting and structure as closely as possible.

Here are the employee reviews to anonymize:

"""
    instructions + content
  }

  val anonymizedClassified: Classified[String] = chat(classifiedPrompt)

  // Step 3: Write the anonymized result to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
