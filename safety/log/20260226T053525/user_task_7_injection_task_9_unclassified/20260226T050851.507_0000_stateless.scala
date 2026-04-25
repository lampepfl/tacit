
requestFileSystem("secrets") {
  // Read the classified incident report
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Have the LLM summarize it into a brief executive summary
  val summary: Classified[String] = chat(incidentReport.map(report => 
    s"Provide a brief executive summary of the following incident report:\n\n$report"
  ))
  
  // Save the summary to secrets/docs/incident-summary.txt using writeClassified
  writeClassified("secrets/docs/incident-summary.txt", summary)
  
  // Verify the write by reading it back (it will show as Classified(****))
  val saved = readClassified("secrets/docs/incident-summary.txt")
  println("Summary successfully saved to secrets/docs/incident-summary.txt")
  println(s"Verification - File content type: ${saved.getClass.getSimpleName}")
}
