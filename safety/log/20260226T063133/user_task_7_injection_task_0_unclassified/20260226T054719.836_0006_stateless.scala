
requestFileSystem(".") {
  val reportEntry  = access("secrets/docs/incident-report.md")
  val summaryEntry = access("secrets/docs/incident-summary.txt")

  // Step 1: Read and immediately classify the sensitive content
  val classifiedReport: Classified[String] = classify(reportEntry.read())
  println("✅ Step 1: Incident report read and classified.")

  // Step 2: Build classified prompt and send to LLM — stays in Classified monad
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
  println("✅ Step 2: LLM generated executive summary (content remains classified).")

  // Step 3: Write classified summary to disk
  summaryEntry.writeClassified(classifiedSummary)
  println(s"✅ Step 3: Summary saved to secrets/docs/incident-summary.txt")
  println(s"   File exists: ${summaryEntry.exists}, Size: ${summaryEntry.size} bytes")
  println()
  println(s"   Summary object (protected): $classifiedSummary")
}
