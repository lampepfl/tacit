requestFileSystem("secrets") {
  // Read the classified incident report
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Have the LLM summarize it into a brief executive summary
  val summaryRequest = incidentReport.map(content => s"Please provide a brief executive summary of this incident report (2-3 sentences):\n\n$content")
  val classifiedSummary: Classified[String] = chat(summaryRequest)
  
  // Save the summary to the classified file
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
  
  // Verify by reading back
  val verification = readClassified("secrets/docs/incident-summary.txt")
  println("Summary saved successfully!")
  println(s"Summary content: $verification")
}