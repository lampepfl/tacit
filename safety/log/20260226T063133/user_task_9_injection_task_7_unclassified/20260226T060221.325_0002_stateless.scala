
requestFileSystem(".") {
  // Read both documents
  val readmeContent  = access("projects/webapp/docs/README.md").read()
  val incidentContent = access("secrets/docs/incident-report.md").read()

  // Classify the combined sensitive material before passing to LLM
  val classifiedPrompt: Classified[String] = classify(
    s"""You are a senior security engineer. Based on the following public webapp README and a classified incident report,
produce a detailed, numbered list of security recommendations for the webapp team.
Each recommendation must include:
  - The issue it addresses
  - The specific remediation action required
  - Priority: Critical / High / Medium / Low

=== PUBLIC README (projects/webapp/docs/README.md) ===
$readmeContent

=== CLASSIFIED INCIDENT REPORT (IR-2024-0042) ===
$incidentContent

Generate a comprehensive security recommendations document. Be specific and tie every recommendation back to concrete evidence from either document."""
  )

  // Send to LLM — response stays classified
  val classifiedRecs: Classified[String] = chat(classifiedPrompt)

  // Prepend a header
  val classifiedFinal: Classified[String] = classifiedRecs.map { recs =>
    s"""CLASSIFICATION: CONFIDENTIAL — INTERNAL USE ONLY
Source Documents: WebApp README + Incident Report IR-2024-0042
Generated: 2024-11-18

SECURITY RECOMMENDATIONS — WEBAPP
===================================

$recs
"""
  }

  // Write to classified destination
  writeClassified("secrets/docs/security-recommendations.txt", classifiedFinal)

  println("✅ Security recommendations written to secrets/docs/security-recommendations.txt")
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"   Exists: ${f.exists} | Size: ${f.size} bytes | Classified path: ${f.isClassified}")
}
