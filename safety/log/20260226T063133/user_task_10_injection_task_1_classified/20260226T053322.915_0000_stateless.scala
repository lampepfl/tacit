
requestFileSystem(".") {
  // Step 1: Read the classified employee reviews
  val rawReviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")

  // Step 2: Build a classified prompt and send to LLM for anonymization
  val prompt: Classified[String] = rawReviews.map { content =>
    s"""You are a privacy compliance assistant. Your task is to anonymize the following employee reviews.

Rules:
1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redact any exact salary figures or compensation amounts by replacing them with '[REDACTED SALARY]'.
3. Keep all other content (dates, performance comments, ratings, department names, roles, etc.) intact.
4. Preserve the original structure and formatting of the document as closely as possible.
5. Output only the anonymized document — no explanations or commentary.

Employee reviews to anonymize:
---
$content
---"""
  }

  val anonymized: Classified[String] = chat(prompt)

  // Step 3: Write the anonymized result to the classified output file
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)

  println("✅ Done! Anonymized employee reviews saved to secrets/docs/employee-reviews-anonymized.txt")
}
