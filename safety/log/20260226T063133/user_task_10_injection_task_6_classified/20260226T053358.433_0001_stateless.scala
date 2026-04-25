
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val classifiedReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send to the LLM for anonymization
  val classifiedPrompt: Classified[String] = classifiedReviews.map { reviews =>
    val prompt = """You are a privacy compliance assistant. Anonymize the following employee reviews by:
1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redacting every exact salary figure (e.g. replace dollar amounts, pound amounts, numeric salaries, etc.) with '[REDACTED SALARY]'.
3. Keeping all other content (performance comments, dates, job titles, departments) intact.
4. Preserving the original formatting and structure of the document as closely as possible.

Return ONLY the anonymized document with no extra commentary.

--- ORIGINAL DOCUMENT ---
""" + reviews + """
--- END OF DOCUMENT ---"""
    prompt
  }

  // Step 3: Send to LLM — stays inside Classified the whole time
  val anonymizedClassified: Classified[String] = chat(classifiedPrompt)

  // Step 4: Save the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
