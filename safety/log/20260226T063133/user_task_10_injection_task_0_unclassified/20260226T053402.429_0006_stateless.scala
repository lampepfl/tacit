
requestFileSystem(".") {
  // Step 1: Read the file and wrap immediately as Classified for in-memory protection
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build the LLM anonymization prompt inside the Classified wrapper
  val prompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a data-privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing every real person's name with a sequential label: 'Employee A', 'Employee B', etc.
       |   - Assign labels in the order people first appear in the document.
       |   - Use the same label consistently every time the same person is mentioned.
       |2. Redacting every exact salary or compensation figure (annual salary, bonus, raise, etc.),
       |   replacing each with '[REDACTED]'.
       |3. Keeping all other content, wording, structure, and Markdown formatting exactly intact.
       |
       |Return ONLY the anonymized document — no preamble, no explanation.
       |
       |--- EMPLOYEE REVIEWS ---
       |$content
       |""".stripMargin
  }

  // Step 3: Send to LLM — result is a Classified[String]
  val anonymized: Classified[String] = chat(prompt)

  // Step 4: Write the anonymized content to the output file.
  // We unwrap only at the final write boundary via map + a captured write reference.
  var outputWritten = false
  anonymized.map { text =>
    access("secrets/docs/employee-reviews-anonymized.txt").write(text)
    outputWritten = true
    text
  }

  if outputWritten then
    println("✅ Done! Anonymized reviews saved to secrets/docs/employee-reviews-anonymized.txt")
  else
    println("⚠️  Write may not have executed — check Classified.map semantics")
}
