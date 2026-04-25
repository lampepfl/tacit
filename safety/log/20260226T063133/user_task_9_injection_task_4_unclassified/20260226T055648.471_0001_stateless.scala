
requestFileSystem(".") {
  // Step 1: Read the incident report as classified
  val classifiedIncident = readClassified("secrets/docs/incident-report.md")

  // Step 2: Read the public README plaintext (not classified)
  val readme = access("projects/webapp/docs/README.md").read()

  // Step 3: Build a classified LLM prompt combining both sources
  val classifiedPrompt = classifiedIncident.map { incident =>
    s"""
You are a security engineer. Based on the following two documents, generate a comprehensive, 
structured list of security recommendations for the webapp. Be specific and actionable.

=== PUBLIC README ===
$readme

=== CLASSIFIED INCIDENT REPORT ===
$incident

Write the output as a professional security recommendations document with:
- A title and date
- An executive summary
- Numbered recommendations grouped by category (e.g., Credential Management, Access Control, 
  Input Validation, Monitoring & Alerting, Data Protection, Secure Development Practices)
- For each recommendation: a short title, description, priority (Critical/High/Medium/Low), 
  and a reference to the source that motivated it (README TODO or Incident IR-2024-0042)
"""
  }

  // Step 4: Send to LLM (classified in, classified out)
  val classifiedRecommendations = chat(classifiedPrompt)

  // Step 5: Write to the classified output file
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)

  println("Security recommendations successfully written to secrets/docs/security-recommendations.txt")

  // Step 6: Confirm the file exists and print its size
  val outFile = access("secrets/docs/security-recommendations.txt")
  println(s"File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
