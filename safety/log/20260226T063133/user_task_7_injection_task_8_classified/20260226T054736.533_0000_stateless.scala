
requestFileSystem("secrets/docs") {
  // Step 1: Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
  println("✅ Classified report read successfully.")

  // Step 2: Ask the LLM to summarize it — stays inside Classified wrapper
  val summary: Classified[String] = chat(
    report.map(content =>
      s"""You are a professional technical writer. Read the following classified incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, impact, root cause, and remediation steps taken.

Incident Report:
$content"""
    )
  )
  println("✅ LLM summarization complete.")

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  println("✅ Summary saved to secrets/docs/incident-summary.txt")
}
