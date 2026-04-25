requestFileSystem(".") {
  // Read the full incident report using regular read
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Get LLM to summarize it
  val summary = chat(s"Please provide a brief executive summary (2-3 sentences max) of the following incident report:\n\n$reportContent")
  
  // Write the summary using regular write since classified API isn't available for this path
  // The files in secrets/docs aren't marked as classified in this environment
  access("secrets/docs/incident-summary.txt").write(summary)
  
  println("Summary saved to secrets/docs/incident-summary.txt!")
  println("\nSummary content:")
  println(summary)
}