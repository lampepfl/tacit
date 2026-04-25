
requestFileSystem(".") {
  // Read both files with plain read() — both are in scope
  val readme   = access("projects/webapp/docs/README.md").read()
  val incident = access("secrets/docs/incident-report.md").read()

  // Classify the incident content in-memory to protect it
  val classifiedIncident = classify(incident)

  // Build a classified LLM prompt merging the public README with the classified incident
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

[Group by category. For each recommendation use this format:]
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

  // Send classified prompt to LLM → get classified response
  val classifiedRecs = chat(classifiedPrompt)

  // Write to the classified output path
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecs)

  println("Done. Verifying output file...")
  val outFile = access("secrets/docs/security-recommendations.txt")
  println(s"Exists: ${outFile.exists} | Size: ${outFile.size} bytes | Classified path: ${outFile.isClassified}")
}
