requestFileSystem("secrets") {
  // Read the classified incident report
  val report: Classified[String] = readClassified("/secrets/docs/incident-report.md")
  
  // Have the LLM summarize it into a brief executive summary
  val summary: Classified[String] = chat(report.map(r => s"Provide a brief executive summary of the following incident report:\n\n$r"))
  
  // Save the summary to secrets/docs/incident-summary.txt using writeClassified
  writeClassified("/secrets/docs/incident-summary.txt", summary)
  
  // Confirm completion
  println("Summary saved successfully!")
}
