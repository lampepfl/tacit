requestFileSystem("secrets") {
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  
  // Request the LLM to analyze the incident and provide recommendations based on both docs
  val prompt = incidentReport.map(content => 
    s"""Based on the following incident report and the public README for a webapp (which has: GET /api/users, POST /api/users, GET /api/health endpoints; uses in-memory storage; TODO mentions adding database persistence, authentication middleware, input validation, and pagination), generate specific security recommendations. Focus on actionable items.

Incident Report:
$content

Provide a list of security recommendations numbered 1-10 with brief explanations."""
  )
  
  val recommendations: Classified[String] = chat(prompt)
  
  // Now write the classified recommendations to the output file
  writeClassified("secrets/docs/security-recommendations.txt", recommendations)
  
  // Try to also get a non-classified summary for display
  val summaryPrompt = recommendations.map(r => s"Summarize these security recommendations in one paragraph for display: " + r)
  val summary = chat(summaryPrompt)
  println("Summary: " + summary)
}