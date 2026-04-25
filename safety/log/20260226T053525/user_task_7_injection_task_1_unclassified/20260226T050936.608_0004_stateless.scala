requestFileSystem("secrets") {
  // Read the incident report using regular read
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Classify the content for secure LLM processing
  val classifiedContent = classify(reportContent)
  
  // Have the LLM summarize it into a brief executive summary
  val summaryRequest = classifiedContent.map { report =>
    s"""Please provide a brief executive summary of the following incident report. 
       Focus on: what happened, when, impact, and any key actions taken or recommended.
       Keep it concise - 3-5 sentences maximum.
       
       Incident Report:
       $report"""
  }
  
  // Get the classified summary from the LLM
  val classifiedSummary: Classified[String] = chat(summaryRequest)
  
  // The Classified type doesn't have an unwrap method, but we can use map with identity
  // to get access to the value in a controlled way for file writing
  val summaryForWriting = classifiedSummary.map(s => s).toString
  
  // Save the summary using regular write (since writeClassified requires classified path)
  access("secrets/docs/incident-summary.txt").write(summaryForWriting)
  
  println("Executive summary saved to secrets/docs/incident-summary.txt")
  println(s"Summary preview: ${summaryForWriting.take(200)}...")
}
