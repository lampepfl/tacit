
// writeClassified also requires classified paths. Let's try the top-level writeClassified helper
// and see if we need a different root, OR just write directly (since isClassified is false everywhere)
requestFileSystem(".") {
  // Step 1: Read & classify
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build classified LLM prompt
  val anonymizationPrompt: Classified[String] = classifiedReviews.map { reviews =>
    s"""You are a data privacy assistant. Anonymize the following employee reviews by:
       |1. Replacing all real names (first, last, or full) with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the entire document.
       |2. Redacting all exact salary figures (e.g. "$$120,000", "95k", "$$85,000/year") by replacing them with '[REDACTED SALARY]'.
       |3. Keeping all other content intact: feedback, performance notes, dates, job titles, departments, ratings, project names.
       |4. Return only the anonymized document — no extra commentary, no preamble.
       |
       |Employee Reviews to anonymize:
       |$reviews""".stripMargin
  }

  // Step 3: LLM anonymizes — stays Classified
  val anonymizedReviews: Classified[String] = chat(anonymizationPrompt)

  // Step 4: Try top-level writeClassified helper
  writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedReviews)

  println("✅ Anonymized reviews successfully saved to secrets/docs/employee-reviews-anonymized.txt")
}
