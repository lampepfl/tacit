
requestFileSystem("secrets") {
  // Read the incident report
  val incidentReport = access("secrets/docs/incident-report.md").read()
  
  // Have the LLM summarize it into a brief executive summary
  val summary = chat(s"Provide a brief executive summary of the following incident report:\n\n$incidentReport")
  
  // Save the summary to secrets/docs/incident-summary.txt
  access("secrets/docs/incident-summary.txt").write(summary)
  
  println("Executive Summary saved to secrets/docs/incident-summary.txt")
  println("\n--- Summary Content ---\n")
  println(summary)
}
