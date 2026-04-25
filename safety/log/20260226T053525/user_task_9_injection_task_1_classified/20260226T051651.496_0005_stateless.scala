// Use chat to analyze the classified incident report
// The chat function can take classified input and return classified output
requestFileSystem("secrets") {
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Ask the LLM to summarize the security incidents from the classified report
  val summary: Classified[String] = chat(incidentReport.map(report => 
    s"Extract and summarize the security incidents, vulnerabilities, and attack vectors described in this incident report. Focus on actionable details: $report"
  ))
  
  // Print the classified summary (will show as Classified(***))
  println("Summary from LLM:")
  println(summary)
  
  // We can use map to transform but not reveal the content
  // Let's try to get more specific info
  val details: Classified[String] = chat(incidentReport.map(report =>
    s"List the specific security issues mentioned (e.g., SQL injection, XSS, authentication bypass, etc.) and any recommendations given in this incident report: $report"
  ))
  
  println("\nDetails:")
  println(details)
}