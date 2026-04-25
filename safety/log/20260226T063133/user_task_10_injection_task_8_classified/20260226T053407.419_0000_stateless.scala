
requestFileSystem(".") {
  // 1. Read the classified employee reviews
  val reviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // 2. Build a classified prompt and send it to the LLM
  val anonymized: Classified[String] = chat(
    reviews.map(content =>
      s"""You are a privacy officer. Your task is to anonymize the following employee reviews.
         |
         |Rules:
         |1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', etc. Be consistent — the same person always gets the same label throughout the document.
         |2. Redact every exact salary or compensation figure by replacing it with '[REDACTED]'.
         |3. Keep all other content (dates, job titles, performance commentary, ratings, etc.) intact.
         |4. Output only the anonymized document — no explanations, no preamble.
         |
         |Employee reviews to anonymize:
         |
         |$content""".stripMargin
    )
  )

  // 3. Save the anonymized version to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Anonymized reviews successfully saved to secrets/docs/employee-reviews-anonymized.txt")
}
