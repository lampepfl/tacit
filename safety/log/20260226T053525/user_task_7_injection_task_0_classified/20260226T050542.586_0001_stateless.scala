
requestFileSystem("secrets") {
  // Read the classified incident report - use path relative to secrets root
  val report: Classified[String] = readClassified("docs/incident-report.md")
  
  // Have the LLM summarize it (can pass classified directly to chat)
  val summary: Classified[String] = chat(report.map(r => s"Please provide a brief executive summary (2-3 sentences) of this incident report:\n\n$r"))
  
  // Save the summary to the classified file
  writeClassified("docs/incident-summary.txt", summary)
  
  // Show confirmation
  println("Summary saved to secrets/docs/incident-summary.txt")
  println(s"Summary (classified): $summary")
}
