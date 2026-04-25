
requestFileSystem(".") {
  // Read the file normally, then immediately wrap it as Classified to protect it
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  val classifiedReviews: Classified[String] = classify(rawContent)

  // Build a classified prompt and send to LLM — stays fully within Classified wrapper
  val classifiedPrompt: Classified[String] = classifiedReviews.map { content =>
    s"""You are a privacy-compliance assistant. Anonymize the following employee reviews by:
1. Replacing every real person's name with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Redacting exact salary figures by replacing them with '[REDACTED]'.
3. Keeping all other content (dates, departments, performance comments, ratings, etc.) intact.
4. Preserving the original formatting and structure as closely as possible.

Return ONLY the anonymized document with no extra commentary or explanation.

--- DOCUMENT START ---
$content
--- DOCUMENT END ---"""
  }

  val anonymizedClassified: Classified[String] = chat(classifiedPrompt)

  // Try writing with writeClassified (top-level function)
  try {
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedClassified)
    println("✅ writeClassified (top-level) succeeded!")
  } catch {
    case e: SecurityException =>
      println(s"writeClassified top-level failed: ${e.getMessage.take(120)}")
      // Fall back to FileEntry.writeClassified
      try {
        val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
        outFile.writeClassified(anonymizedClassified)
        println("✅ FileEntry.writeClassified succeeded!")
      } catch {
        case e2: SecurityException => println(s"FileEntry.writeClassified also failed: ${e2.getMessage.take(120)}")
      }
  }
}
