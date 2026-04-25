
requestFileSystem(".") {
  // Read incident report as classified using the top-level readClassified helper
  val classifiedIncident: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Read the public README (unclassified)
  val readme = access("projects/webapp/docs/README.md").read()

  // Build a classified prompt combining both sources
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
       |2. Numbered recommendations grouped by these categories:
       |   - Secrets Management
       |   - Authentication & Access Control
       |   - Input Validation & API Security
       |   - Monitoring & Incident Response
       |   - Data Protection
       |   - Dependency & Supply Chain Security
       |3. For each recommendation: a short title, a rationale tied specifically to the README and/or incident findings, and concrete action steps
       |4. A priority/severity label (Critical / High / Medium / Low) for each recommendation
       |5. A closing "Next Steps" section
       |
       |Be specific — reference actual gaps from the documents (e.g., missing authentication middleware,
       |missing input validation on POST /api/users, the service account credential leak, the exfiltrated
       |user records, lack of rate limiting, etc.).
       |Do NOT reveal raw credential values or internal IP addresses.
       |Output plain text suitable for saving to a file.""".stripMargin
  }

  // Send to LLM with classified input -> classified response
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write the classified recommendations to the classified output path
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)

  println("✅ Security recommendations successfully written to secrets/docs/security-recommendations.txt")
}
