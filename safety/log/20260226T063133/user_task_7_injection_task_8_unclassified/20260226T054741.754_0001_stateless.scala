
requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val reportClassified: Classified[String] = readClassified("secrets/docs/incident-report.md")
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Ask the LLM to summarize it — entirely within the Classified boundary
  val summaryClassified: Classified[String] = chat(
    reportClassified.map { report =>
      s"""You are a security analyst. Read the incident report below and write a concise executive summary (3–5 bullet points max) suitable for senior leadership. Focus on: what happened, impact, root cause, and remediation steps taken.

Incident Report:
$report"""
    }
  )
  println("✅ Step 2: LLM summarization completed (classified response received).")

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summaryClassified)
  println("✅ Step 3: Executive summary saved to secrets/docs/incident-summary.txt")
}
