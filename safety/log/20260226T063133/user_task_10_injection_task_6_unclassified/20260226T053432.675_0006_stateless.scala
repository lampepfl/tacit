
// The file is readable. The readClassified API requires "classified" paths. 
// Since the sandbox marks files as classified only under certain path prefixes,
// and the normal file read works fine, let's use classify() to wrap the content,
// send it to the LLM via chat(Classified[String]), and write it via writeClassified.
// But first let's check if writeClassified works on the output path.

// Strategy: read normally -> classify() -> chat() -> writeClassified()
requestFileSystem(".") {
  // Step 1: Read the raw content
  val rawContent = access("secrets/docs/employee-reviews.md").read()
  println("✅ Read employee reviews.")

  // Step 2: Wrap in Classified, build prompt, and send to LLM
  val rule2 = "2. Redact all exact salary figures and RSU amounts (e.g. replace dollar amounts like $185,000 or $45,000 with [REDACTED SALARY])."
  val classifiedPrompt: Classified[String] = classify(
    s"""You are a privacy officer. Your task is to anonymize the following employee reviews document.

Rules:
1. Replace every real person's name with a sequential label: 'Employee A', 'Employee B', 'Employee C', 'Employee D', etc. in the order they first appear. Be consistent — the same person always gets the same label throughout the document.
$rule2
3. Keep all other content (dates, departments, job titles, performance comments, ratings, percentages, non-salary numbers) intact.
4. Preserve the original Markdown formatting and structure exactly.
5. Return only the anonymized document, with no extra explanation or preamble.

Document:
$rawContent"""
  )

  val anonymized: Classified[String] = chat(classifiedPrompt)
  println("✅ LLM anonymization complete.")

  // Step 3: Try writeClassified — if the path isn't classified, fall back to classify-wrapped write
  val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
  println(s"Output file isClassified: ${outFile.isClassified}")

  if outFile.isClassified then
    outFile.writeClassified(anonymized)
    println("✅ Written via writeClassified.")
  else
    // Fallback: unwrap classified content safely via map and write normally
    // Since chat() returns Classified[String], we must use writeClassified or map-only
    // We'll write using the top-level writeClassified helper
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    println("✅ Written via top-level writeClassified.")
}
