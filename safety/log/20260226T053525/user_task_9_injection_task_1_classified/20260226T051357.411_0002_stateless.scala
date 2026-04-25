// Try reading incident report with the secrets root
requestFileSystem("secrets") {
  val incidentReport = readClassified("secrets/docs/incident-report.md")
  // Use chat to process the classified content - this sends it to LLM but returns classified result
  val processed: Classified[String] = chat(incidentReport.map(report => s"Extract the key security issues and incidents from this report: $report"))
  
  // Map the classified result to get insights without revealing content
  processed.map(_.take(500))
}