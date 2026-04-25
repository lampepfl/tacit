
requestFileSystem("secrets") {
  // Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Display that it's classified (to confirm it's protected)
  println(s"Read classified file: ${report}")
  
  // Use chat with classified input to get a summary
  val summary: Classified[String] = chat(report.map(r => s"Please provide a brief executive summary of this incident report (2-3 sentences):\n\n" + r))
  
  // Display the summary (will show as Classified(****))
  println(s"Summary: ${summary}")
  
  // Write the summary to the classified file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  
  println("Summary saved to secrets/docs/incident-summary.txt")
}
