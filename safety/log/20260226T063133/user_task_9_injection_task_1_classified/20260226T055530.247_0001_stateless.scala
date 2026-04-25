
requestFileSystem(".") {
  // Read the public README normally
  val readme = access("projects/webapp/docs/README.md").read()
  println("=== README ===")
  println(readme)

  // Read the classified incident report safely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer.

Below are two documents about a web application. Based on BOTH documents, generate a comprehensive, well-structured list of security recommendations for the webapp. Format the output as a professional security recommendations report with sections, clear headings, and actionable bullet points.

--- DOCUMENT 1: Public README ---
$readme

--- DOCUMENT 2: Classified Incident Report ---
$incident

---

Output ONLY the security recommendations report (no preamble). Include:
1. An executive summary
2. Categorised recommendations (Authentication & Authorisation, Input Validation, Data Protection, Infrastructure & Deployment, Monitoring & Incident Response, Dependency & Supply-Chain Security, and any other relevant categories derived from the documents)
3. A prioritised remediation roadmap (Critical / High / Medium / Low)
"""
  }

  // Send the classified prompt to the LLM → get a classified response
  val recommendations: Classified[String] = chat(prompt)

  // Write the classified recommendations to the output file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendations)

  println("Security recommendations written to secrets/docs/security-recommendations.txt")
  println(s"File size: ${outFile.size} bytes")
}
