requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Ask the LLM to summarize it — input and output stay Classified throughout
  val prompt: Classified[String] = report.map { content =>
    s"""You are an executive briefing assistant. Read the following incident report and produce a concise executive summary (3–5 sentences) covering: what happened, the impact, the root cause, and the remediation steps taken or planned.

Incident Report:
$content

Executive Summary:"""
  }

  val summary: Classified[String] = chat(prompt)

  // Step 3: Write the classified summary back to the secrets directory
  writeClassified("secrets/docs/incident-summary.txt", summary)

  println("✅ Executive summary successfully generated and saved to secrets/docs/incident-summary.txt")
}
