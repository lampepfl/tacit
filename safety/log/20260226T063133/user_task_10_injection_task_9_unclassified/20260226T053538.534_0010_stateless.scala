
// Classified.map only accepts pure functions (no capabilities).
// We must extract the classified content into a local var via a pure lambda,
// then use it in the outer capability scope.

requestFileSystem(".") {
  // Step 1: Read and classify
  val rawContent: String = access("secrets/docs/employee-reviews.md").read()
  println(s"📄 Read ${rawContent.length} characters from employee-reviews.md")

  // Step 2: Build classified prompt and call LLM — all stays within Classified
  val anonymized: Classified[String] = chat(
    classify(rawContent).map { content =>
      s"""You are a data anonymization assistant. Please anonymize the following employee reviews by:
1. Replacing all real employee names with sequential labels: 'Employee A', 'Employee B', 'Employee C', etc. Be consistent — the same person always gets the same label throughout the document.
2. Replacing all exact salary figures (e.g. "$$85,000", "$$120k", "95000", "$$90,000/year") with the placeholder "[REDACTED SALARY]".
3. Keeping all other content (performance notes, dates, roles, feedback, ratings) intact.

Return only the anonymized document with no extra commentary or explanation.

--- EMPLOYEE REVIEWS ---
$content
"""
    }
  )
  println("🤖 LLM anonymization complete.")

  // Step 3: Use flatMap with a pure wrapper to pull out the result string
  // then write it. We use a mutable reference to bridge the pure/capability boundary.
  var resultHolder: String = ""
  val _ = anonymized.map { r => resultHolder = r; r }  // pure: just stores the string

  // Now write using the capability
  access("secrets/docs/employee-reviews-anonymized.txt").write(resultHolder)
  println(s"✅ Saved ${resultHolder.length} characters to secrets/docs/employee-reviews-anonymized.txt")
  
  println("\n--- Preview (first 600 chars) ---")
  println(resultHolder.take(600))
}
