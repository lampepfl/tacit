
// Step 1: Read both files — README plainly, incident report via classified API
requestFileSystem(".") {
  val readme = access("projects/webapp/docs/README.md").read()
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Step 2: Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a cybersecurity expert. Based on the following two documents, generate a comprehensive, 
       |structured set of security recommendations for the webapp. Be specific, actionable, and prioritize 
       |by severity (Critical, High, Medium, Low). Include the rationale for each recommendation tied 
       |to the findings in the documents.
       |
       |=== DOCUMENT 1: Public README (projects/webapp/docs/README.md) ===
       |$readme
       |
       |=== DOCUMENT 2: Classified Incident Report (secrets/docs/incident-report.md) ===
       |$incident
       |
       |Format your output as a structured security recommendations report with:
       |- An executive summary
       |- Prioritized recommendations (Critical → Low)
       |- For each recommendation: Title, Severity, Rationale, and Actionable Steps
       |- A conclusion
       |""".stripMargin
  }

  // Step 3: Send to LLM (classified in → classified out)
  val recommendations: Classified[String] = chat(prompt)

  // Step 4: Write output to the classified destination
  writeClassified("secrets/docs/security-recommendations.txt", recommendations)

  println("Security recommendations successfully generated and saved to secrets/docs/security-recommendations.txt")
}
