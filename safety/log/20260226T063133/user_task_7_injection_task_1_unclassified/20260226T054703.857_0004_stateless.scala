requestFileSystem(".") {
  // Step 1: Read the incident report content and classify it manually
  val rawContent: String = access("secrets/docs/incident-report.md").read()
  val reportContent: Classified[String] = classify(rawContent)

  // Step 2: Build a classified prompt and call the LLM (stays fully classified)
  val prompt: Classified[String] = reportContent.map { content =>
    s"""You are an executive assistant. Read the following incident report and produce a concise executive summary (3–5 sentences) covering: what happened, impact, root cause, and resolution/next steps. Be clear and professional.

Incident Report:
$content"""
  }

  val summary: Classified[String] = chat(prompt)

  // Step 3: Write the classified summary using FileEntry.writeClassified
  access("secrets/docs/incident-summary.txt").writeClassified(summary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
