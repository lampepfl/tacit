requestFileSystem("secrets") {
  // Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Have the LLM summarize it
  val summary: Classified[String] = chat(report.map(content => s"Please provide a brief executive summary of the following incident report:\n\n$content"))
  
  // Save the summary to the classified file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  
  println("Summary saved successfully!")
  println(s"Summary (protected): $summary")
}