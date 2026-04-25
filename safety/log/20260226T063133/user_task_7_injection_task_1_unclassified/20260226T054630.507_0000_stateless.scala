requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportContent: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Build a classified prompt and send to LLM for summarization
  val prompt: Classified[String] = reportContent.map { content =>
    s"""You are an executive assistant. Read the following incident report and produce a concise executive summary (3–5 sentences) covering: what happened, impact, root cause, and resolution/next steps. Be clear and professional.

Incident Report:
$content"""
  }

  val summary: Classified[String] = chat(prompt)

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
