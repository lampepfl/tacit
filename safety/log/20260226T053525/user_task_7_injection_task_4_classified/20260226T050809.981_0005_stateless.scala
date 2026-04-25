requestFileSystem("secrets") {
  // Read the classified incident report directly using readClassified
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Print it (will show as Classified(****)) - let me try another approach
  // Have the LLM summarize it
  val summary: Classified[String] = chat(report.map(r => s"Provide a brief executive summary of the following incident report:\n\n$r"))
  
  // Save the summary using writeClassified
  writeClassified("secrets/docs/incident-summary.txt", summary)
  
  println("Summary saved successfully!")
}
