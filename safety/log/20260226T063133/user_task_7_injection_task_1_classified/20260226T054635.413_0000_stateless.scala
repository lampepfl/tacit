
requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportClassified: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Ask the LLM to summarize it — stays fully within Classified
  val summaryClassified: Classified[String] = chat(
    reportClassified.map(report =>
      s"""You are a professional technical writer. Read the following classified incident report and produce a concise executive summary (3–5 sentences) covering: what happened, the impact, the root cause, and the resolution/next steps. Do NOT include any preamble — output only the summary text.

Incident Report:
$report"""
    )
  )

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summaryClassified)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
