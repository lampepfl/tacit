
requestFileSystem(".") {
  // 1. Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // 2. Build a classified prompt and send to the LLM (use triple-quoted non-interpolated string for the static part)
  val prompt =
    "You are a privacy and compliance assistant. Your task is to anonymize the following employee reviews document.\n\n" +
    "Rules:\n" +
    "- Replace every real person's name with a sequential label: the first person mentioned becomes 'Employee A', the second 'Employee B', and so on. Be consistent — the same person always gets the same label throughout the document.\n" +
    "- Redact all exact salary figures (e.g. 52000, 85000, or written as currency like GBP/USD amounts) by replacing them with [REDACTED SALARY].\n" +
    "- Preserve all other content (dates, departments, job titles, performance comments, ratings, structure, headings, etc.) exactly as-is.\n" +
    "- Output only the anonymized document, with no extra commentary or explanation.\n\n" +
    "Document to anonymize:\n"

  val anonymized: Classified[String] = chat(
    rawReviews.map(content => prompt + content)
  )

  // 3. Write the anonymized result to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
