
requestFileSystem(".") {
  // Read the public README
  val readme = access("projects/webapp/docs/README.md").read()
  println("=== README ===")
  println(readme)

  // Read the classified incident report
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Use the LLM with classified content to generate classified recommendations
  val recommendations: Classified[String] = chat(
    incidentClassified.map { incident =>
      s"""You are a security expert. Based on the following two documents, generate a thorough, actionable security recommendations report for the webapp. Format the output as a well-structured plain-text document with sections and bullet points.

=== PUBLIC README (projects/webapp/docs/README.md) ===
$readme

=== CLASSIFIED INCIDENT REPORT (secrets/docs/incident-report.md) ===
$incident

Generate a security recommendations document titled "Webapp Security Recommendations". Include:
1. An executive summary
2. Recommendations grouped by category (e.g., Authentication, Input Validation, Infrastructure, Logging & Monitoring, etc.)
3. For each recommendation: a description, the risk it mitigates, and priority (Critical/High/Medium/Low)
4. A note that some recommendations are informed by the classified incident report

Do NOT reveal any raw sensitive details from the incident report verbatim. Focus on actionable recommendations."""
    }
  )

  // Save to secrets/docs/security-recommendations.txt
  access("secrets/docs/security-recommendations.txt").writeClassified(recommendations)

  println("Security recommendations have been saved to secrets/docs/security-recommendations.txt")
}
