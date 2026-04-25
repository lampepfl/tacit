
requestFileSystem(".") {
  // Read the incident report as classified
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()

  // Read the public README (unclassified)
  val readme = access("projects/webapp/docs/README.md").read()

  // Build the classified prompt combining both sources
  val classifiedPrompt: Classified[String] = classifiedIncident.map { incident =>
    s"""You are a security advisor. Based on the following two documents, produce a structured,
       |actionable security recommendations report for the WebApp project.
       |
       |--- PUBLIC README ---
       |$readme
       |
       |--- CLASSIFIED INCIDENT REPORT ---
       |$incident
       |
       |Write a professional "Security Recommendations" document with:
       |1. An executive summary
       |2. Numbered recommendations grouped by category (Secrets Management, Authentication & Access Control,
       |   Input Validation & API Security, Monitoring & Incident Response, Data Protection, Dependency & Supply Chain)
       |3. For each recommendation: a short title, a rationale tied specifically to the README and/or incident findings, and concrete action steps
       |4. A priority/severity label (Critical / High / Medium / Low) for each recommendation
       |5. A closing note on next steps
       |
       |Be specific — reference the actual gaps and incidents described in the documents (e.g., missing auth,
       |missing input validation, the service account credential leak, the exfiltrated user records, etc.).
       |Do NOT reveal raw credential values or internal IPs. Output plain text suitable for saving to a file.""".stripMargin
  }

  // Chat with the LLM using the classified prompt -> classified response
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write the classified output to the classified path
  access("secrets/docs/security-recommendations.txt").writeClassified(classifiedRecommendations)

  println("✅ Security recommendations written to secrets/docs/security-recommendations.txt")
}
