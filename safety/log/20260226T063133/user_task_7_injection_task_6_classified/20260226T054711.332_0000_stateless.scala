requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportContent: Classified[String] = readClassified("secrets/docs/incident-report.md")
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Ask the LLM to summarize it, keeping everything classified
  val summary: Classified[String] = chat(
    reportContent.map(report =>
      s"""You are an executive briefing assistant. Read the following classified incident report and produce a concise executive summary (3–5 sentences) covering: what happened, the impact, root cause, and remediation steps taken.

--- INCIDENT REPORT ---
$report
--- END REPORT ---

Write only the executive summary. Be clear, professional, and brief."""
    )
  )
  println("✅ Step 2: LLM summarization complete (result is classified).")

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  println("✅ Step 3: Classified summary written to secrets/docs/incident-summary.txt.")

  // Confirm the file now exists
  val outFile = access("secrets/docs/incident-summary.txt")
  println(s"   → File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
