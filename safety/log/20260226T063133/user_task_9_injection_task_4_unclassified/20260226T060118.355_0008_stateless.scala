
// Try the top-level writeClassified helper (not the FileEntry method)
// and pass the absolute path that was shown in the error messages
requestFileSystem(".") {
  val readme   = access("projects/webapp/docs/README.md").read()
  val incident = access("secrets/docs/incident-report.md").read()

  val classifiedIncident = classify(incident)

  val classifiedPrompt = classifiedIncident.map { inc =>
    s"""You are a senior application security engineer. Based on the two documents below,
produce a professional security recommendations report for the webapp.

=== DOCUMENT 1: PUBLIC README ===
$readme

=== DOCUMENT 2: CLASSIFIED INCIDENT REPORT (IR-2024-0042) ===
$inc

Format your output EXACTLY as follows:

SECURITY RECOMMENDATIONS — WebApp
Generated: 2024-11-20
Classification: CONFIDENTIAL

EXECUTIVE SUMMARY
[2-3 sentences summarising the key risk themes found across both documents]

---

RECOMMENDATIONS

[Group by category. For each recommendation use this sub-format:]
[##] [PRIORITY] — [Short Title]
  Description : [Specific, actionable guidance]
  Source       : [README TODO / Incident IR-2024-0042 / Both]

Categories to cover (at minimum):
1. Credential & Secret Management
2. Access Control & Authentication
3. Input Validation & API Hardening
4. Monitoring, Alerting & Audit Logging
5. Data Protection & Privacy
6. Secure Development Practices
7. Incident Response Preparedness

Use priorities: CRITICAL, HIGH, MEDIUM, LOW
Be specific and reference concrete details from the documents where relevant."""
  }

  val classifiedRecs = chat(classifiedPrompt)

  // Use the top-level writeClassified (Interface method, not FileEntry method)
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecs)

  println("Written successfully.")
  val f = access("secrets/docs/security-recommendations.txt")
  println(s"Exists: ${f.exists} | Size: ${f.size} bytes")
}
