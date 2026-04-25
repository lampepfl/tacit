requestFileSystem(".") {
  // Read the full incident report
  val reportContent = access("secrets/docs/incident-report.md").read()
  
  // Get LLM to summarize it
  val summary = chat(s"Please provide a brief executive summary (2-3 sentences max) of the following incident report:\n\n$reportContent")
  
  println("Summary:")
  println(summary)
  
  // Try to write to classified file - let's try both approaches
  // First let's see if we can create a classified path
  val summaryClassified = classify(summary)
  
  // Try writing with writeClassified
  try {
    writeClassified("secrets/docs/incident-summary.txt", summaryClassified)
    println("\nSuccessfully wrote to incident-summary.txt!")
  } catch {
    case e: Exception => println(s"Error: ${e.getMessage}")
  }
}