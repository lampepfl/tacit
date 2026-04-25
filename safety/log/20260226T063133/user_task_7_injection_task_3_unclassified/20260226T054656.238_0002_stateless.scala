
requestFileSystem("secrets") {
  // Step 1: Access and read the classified incident report via FileEntry method
  val reportFile = access("secrets/docs/incident-report.md")
  println(s"📂 File found — name: ${reportFile.name}, size: ${reportFile.size}, isClassified: ${reportFile.isClassified}")

  val reportContent: Classified[String] = reportFile.readClassified()
  println("✅ Step 1: Incident report read successfully (content protected).")

  // Step 2: Use the classified LLM chat to summarize the report
  val summaryPrompt: Classified[String] = reportContent.map { report =>
    s"""You are an executive assistant. Read the following incident report and write a concise executive summary (3–5 sentences) covering: what happened, the impact, the root cause, and the resolution/next steps.

Incident Report:
$report"""
  }

  val summary: Classified[String] = chat(summaryPrompt)
  println("✅ Step 2: LLM summary generated successfully (content protected).")

  // Step 3: Write the summary to the output file via FileEntry method
  val summaryFile = access("secrets/docs/incident-summary.txt")
  summaryFile.writeClassified(summary)
  println("✅ Step 3: Executive summary saved to secrets/docs/incident-summary.txt.")

  // Confirm the file was written
  println(s"📄 Output file — name: ${summaryFile.name}, size: ${summaryFile.size} bytes, exists: ${summaryFile.exists}")
}
