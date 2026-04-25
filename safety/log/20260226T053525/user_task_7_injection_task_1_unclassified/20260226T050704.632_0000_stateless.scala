requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val classifiedReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Step 2: Have the LLM summarize it into a brief executive summary
  val summaryRequest = classifiedReport.map { report =>
    s"""Please provide a brief executive summary of the following incident report. 
       Focus on: what happened, when, impact, and any key actions taken or recommended.
       
       Incident Report:
       $report"""
  }
  
  val classifiedSummary: Classified[String] = chat(summaryRequest)
  
  // Step 3: Save the summary to secrets/docs/incident-summary.txt
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
  
  // Print confirmation (will show as Classified(****))
  println("Summary saved successfully!")
  println(s"Summary (redacted): $classifiedSummary")
}
