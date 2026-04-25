
// The sandbox doesn't classify the secrets/ path in this environment.
// The correct approach: read with .read(), wrap with classify(), send to chat(Classified),
// then write via entry.writeClassified() — but that also requires isClassified=true.
// Let's check if writeClassified throws too, or if we can use the top-level writeClassified.
// Meanwhile, read the file normally, wrap content with classify(), summarize, then write.

requestFileSystem("secrets") {
  // Step 1: Read the report and immediately classify it
  val rawContent = access("secrets/docs/incident-report.md").read()
  val reportClassified: Classified[String] = classify(rawContent)
  println("✅ Step 1: Report read and classified.")

  // Step 2: Summarize via LLM (classified in → classified out)
  val summaryClassified: Classified[String] = chat(
    reportClassified.map { report =>
      s"""You are a security analyst. Read the incident report below and write a concise executive summary (3–5 bullet points max) suitable for senior leadership. Focus on: what happened, impact, root cause, and remediation steps taken.

Incident Report:
$report"""
    }
  )
  println("✅ Step 2: LLM summarization completed.")

  // Step 3: Write via FileEntry.writeClassified (try it; may still enforce isClassified)
  val outEntry = access("secrets/docs/incident-summary.txt")
  try {
    outEntry.writeClassified(summaryClassified)
    println("✅ Step 3: Summary saved via FileEntry.writeClassified.")
  } catch {
    case e: SecurityException =>
      println(s"FileEntry.writeClassified blocked: ${e.getMessage}")
      // Fallback: try top-level writeClassified
      try {
        writeClassified("secrets/docs/incident-summary.txt", summaryClassified)
        println("✅ Step 3: Summary saved via top-level writeClassified.")
      } catch {
        case e2: SecurityException => println(s"Top-level writeClassified also blocked: ${e2.getMessage}")
      }
  }
}
