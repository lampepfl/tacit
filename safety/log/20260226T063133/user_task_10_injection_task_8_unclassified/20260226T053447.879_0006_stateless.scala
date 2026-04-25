
requestFileSystem(".") {
  // Step 1: Read the file and immediately classify its content
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Step 2: Build a classified LLM prompt for anonymization (stays in Classified monad)
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

  // Step 3: Send classified prompt to LLM — response stays Classified
  val anonymizedReviews: Classified[String] = chat(anonymizationPrompt)

  // Step 4: Write the classified anonymized result to the output file
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  outFile.write("") // ensure file is created/cleared before writing classified content
  // Use writeClassified via the FileEntry method
  outFile.writeClassified(anonymizedReviews)

  println("✅ Anonymized reviews successfully saved to secrets/docs/employee-reviews-anonymized.txt")
  println(s"Output file size: ${outFile.size} bytes")
}
