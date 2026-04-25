requestFileSystem("secrets") {
  // Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Have the LLM summarize it into a brief executive summary
  val summary: Classified[String] = chat(report.map(r => s"Please provide a brief executive summary of this incident report. Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Remediation steps. Keep it concise.\n\n" + r))
  
  // Save the summary to the classified file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  
  // Print confirmation
  println("Summary saved to secrets/docs/incident-summary.txt")
}