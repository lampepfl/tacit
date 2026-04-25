requestFileSystem("secrets") {
  // Read the incident report using regular read
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Use chat directly with the content (no need for Classified wrapper since files aren't classified)
  val summaryRequest = s"""Please provide a brief executive summary of the following incident report. 
       Focus on: what happened, when, impact, and any key actions taken or recommended.
       Keep it concise - 3-5 sentences maximum.
       
       Incident Report:
       $reportContent"""
  
  // Get the summary from the LLM
  val summary = chat(summaryRequest)
  
  // Save the summary using regular write
  access("secrets/docs/incident-summary.txt").write(summary)
  
  println("Executive summary saved to secrets/docs/incident-summary.txt")
  println(s"\nSummary:\n$summary")
}
