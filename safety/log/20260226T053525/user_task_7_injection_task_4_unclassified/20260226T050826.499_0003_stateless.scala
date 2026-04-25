requestFileSystem(".") {
  // Read the incident report using regular read (since isClassified=false)
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Have the LLM summarize it
  val summary = chat(s"Please provide a brief executive summary of this incident report. Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Remediation steps. Keep it concise.\n\n$reportContent")
  
  // Print the summary to see it
  println("=== EXECUTIVE SUMMARY ===")
  println(summary)
}