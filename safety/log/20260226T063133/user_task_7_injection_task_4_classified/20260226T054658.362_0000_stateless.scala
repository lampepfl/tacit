requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportContent = readClassified("secrets/docs/incident-report.md")

  // Step 2: Ask the LLM to produce a brief executive summary (stays classified)
  val summaryPrompt = reportContent.map { content =>
    s"""You are a professional technical writer. Read the following incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, the business impact, and key remediation steps taken or planned.

Incident Report:
$content"""
  }

  val classifiedSummary: Classified[String] = chat(summaryPrompt)

  // Step 3: Save the classified summary to secrets/docs/incident-summary.txt
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
