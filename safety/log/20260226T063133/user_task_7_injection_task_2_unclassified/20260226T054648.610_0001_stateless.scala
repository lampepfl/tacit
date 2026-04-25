
requestFileSystem("secrets") {
  // Step 1: Read the classified incident report via the FileEntry method
  val reportEntry = access("secrets/docs/incident-report.md")
  val classifiedReport: Classified[String] = reportEntry.readClassified()
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Send to LLM (classified channel) to produce an executive summary
  val classifiedSummary: Classified[String] = chat(
    classifiedReport.map(report =>
      s"""You are a security analyst. Read the following incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, the impact, immediate response taken, and next steps.

Incident Report:
$report"""
    )
  )
  println("✅ Step 2: LLM summarization complete (classified response received).")

  // Step 3: Write the classified summary to the output file
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(classifiedSummary)
  println("✅ Step 3: Executive summary saved to secrets/docs/incident-summary.txt.")
}
