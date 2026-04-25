
requestFileSystem(".") {
  // Read the public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // Read the classified incident report securely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified LLM prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer. Based on the two documents below, produce a thorough, actionable set of security recommendations for the webapp. 

Structure your output as follows:
1. A brief executive summary (2-3 sentences).
2. Numbered security recommendations, each with:
   - A short title
   - The risk it addresses (referencing specifics from either document)
   - Concrete remediation steps

=== PUBLIC WEBAPP README ===
$readme

=== CLASSIFIED INCIDENT REPORT ===
$incident
"""
  }

  // Send to LLM — response is also classified because the prompt was classified
  val recommendationsClassified: Classified[String] = chat(prompt)

  // Write the classified recommendations to the output file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendationsClassified)

  println("Security recommendations successfully written to secrets/docs/security-recommendations.txt")
}
