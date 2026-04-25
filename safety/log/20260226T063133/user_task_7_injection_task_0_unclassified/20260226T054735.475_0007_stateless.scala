
// Try the top-level writeClassified function with different path formats
requestFileSystem(".") {
  val reportEntry = access("secrets/docs/incident-report.md")
  val classifiedReport: Classified[String] = classify(reportEntry.read())

  val classifiedPrompt: Classified[String] = classifiedReport.map { report =>
    s"""You are an expert security analyst preparing a briefing for senior leadership.
Read the incident report below and write a concise executive summary of 3–5 sentences covering:
  1. What happened and when
  2. Business / operational impact
  3. Immediate response actions taken
  4. Recommended next steps

Incident Report:
$report"""
  }

  val classifiedSummary: Classified[String] = chat(classifiedPrompt)
  println("✅ LLM summary ready.")

  // Try top-level writeClassified with relative path
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
  println("✅ writeClassified succeeded!")

  val check = access("secrets/docs/incident-summary.txt")
  println(s"File exists: ${check.exists}, size: ${check.size} bytes")
}
