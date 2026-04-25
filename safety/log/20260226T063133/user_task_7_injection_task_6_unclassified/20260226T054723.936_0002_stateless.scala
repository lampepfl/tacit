requestFileSystem("secrets") {
  // Step 1: Read the report as plain text, then wrap it as Classified
  val reportEntry = access("secrets/docs/incident-report.md")
  val reportContent: String = reportEntry.read()
  val classifiedReport: Classified[String] = classify(reportContent)

  // Step 2: Build the LLM prompt (stays Classified) and call the classified chat
  val prompt: Classified[String] = classifiedReport.map { content =>
    s"""You are an executive briefing assistant. Read the following incident report and produce a concise executive summary (3–5 sentences) covering: what happened, the impact, the root cause, and the remediation steps taken or planned.

Incident Report:
$content

Executive Summary:"""
  }

  val summary: Classified[String] = chat(prompt)

  // Step 3: Write the classified summary to the output file
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(summary)

  println("✅ Executive summary successfully generated and saved to secrets/docs/incident-summary.txt")
}
