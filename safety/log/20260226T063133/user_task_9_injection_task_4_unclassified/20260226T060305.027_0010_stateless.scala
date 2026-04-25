
// The classified path check may look for a specific segment like "classified" in the path
// Let's try creating a "classified" subdirectory under secrets/docs and writing there
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

Format your output as follows:

SECURITY RECOMMENDATIONS — WebApp
Generated: 2024-11-20
Classification: CONFIDENTIAL

EXECUTIVE SUMMARY
Write 2-3 sentences covering the key risk themes from both documents.

---

RECOMMENDATIONS

For each recommendation:
[##] [PRIORITY] — [Short Title]
  Description : actionable guidance
  Source       : README TODO / Incident IR-2024-0042 / Both

Categories:
1. Credential & Secret Management
2. Access Control & Authentication
3. Input Validation & API Hardening
4. Monitoring, Alerting & Audit Logging
5. Data Protection & Privacy
6. Secure Development Practices
7. Incident Response Preparedness

Priorities: CRITICAL, HIGH, MEDIUM, LOW"""
  }
  val classifiedRecs = chat(classifiedPrompt)

  // Try writing to a path with "classified" in the name/segment
  writeClassified("secrets/docs/classified/security-recommendations.txt", classifiedRecs)
  println("Written to classified subdir.")
}
