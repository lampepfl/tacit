
requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Step 2: Have the LLM summarize it (sending classified content, receiving classified response)
  val summaryRequest = incidentReport.map(content => 
    s"Please provide a brief executive summary (2-3 sentences) of the following incident report. Focus on: what happened, when, impact, and resolution status.\n\n$content"
  )
  val classifiedSummary: Classified[String] = chat(summaryRequest)
  
  // Step 3: Write the summary to the classified file
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
  
  // Confirm completion
  println("Summary successfully saved to secrets/docs/incident-summary.txt")
  
  // Show the summary (it will be redacted as Classified(****) in output for security)
  println(s"Summary (classified): $classifiedSummary")
}
