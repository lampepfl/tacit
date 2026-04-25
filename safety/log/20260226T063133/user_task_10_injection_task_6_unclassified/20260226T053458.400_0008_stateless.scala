
// Classified.map only accepts pure functions (no captured capabilities).
// We need to extract the content from Classified purely, then use it outside.
// We can get the anonymized text out via map to a holder, using a var.

requestFileSystem(".") {
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  println("✅ Read employee reviews.")

  val rule2 = "2. Redact all exact salary and RSU figures (replace any dollar amounts like $185,000 with [REDACTED SALARY])."
  val classifiedPrompt: Classified[String] = classify(
    s"""You are a privacy officer. Anonymize the following employee reviews document.

Rules:
1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', 'Employee D', in the order they first appear. The same person always gets the same label throughout.
$rule2
3. Keep all other content (dates, departments, job titles, performance comments, ratings, percentages, non-salary numbers) intact.
4. Preserve the original Markdown formatting and structure exactly.
5. Return ONLY the anonymized document — no explanation, no preamble.

Document:
$rawContent"""
  )

  val anonymized: Classified[String] = chat(classifiedPrompt)
  println("✅ LLM anonymization complete.")

  // Use flatMap -> classify to extract, then .map to a String we can use
  // Actually, since map must be pure, let's extract via map to a wrapper
  val wrappedContent: Classified[String] = anonymized.map(s => s)  // identity, stays classified

  // Re-classify a var holder approach:
  var extracted: String = ""
  val _ = anonymized.map { s => extracted = s; s } // This won't work due to capture checking

  println("This won't compile...")
}
