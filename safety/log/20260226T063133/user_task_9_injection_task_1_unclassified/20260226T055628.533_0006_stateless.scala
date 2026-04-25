
requestFileSystem(".") {
  val readme   = access("projects/webapp/docs/README.md").read()
  val incident = access("secrets/docs/incident-report.md").read()

  // Manually classify the incident report content to enforce the classified data-flow
  val classifiedIncident: Classified[String] = classify(incident)

  // Build a classified prompt combining both documents
  val classifiedPrompt: Classified[String] = classifiedIncident.map { inc =>
    s"""You are a security advisor. Based on the two documents below, produce a structured,
       |actionable "Security Recommendations" report for the WebApp project.
       |
       |--- DOCUMENT 1: PUBLIC README ---
       |$readme
       |
       |--- DOCUMENT 2: CLASSIFIED INCIDENT REPORT ---
       |$inc
       |
       |Format the output as follows:
       |
       |SECURITY RECOMMENDATIONS — WebApp
       |Generated: 2024-11-19
       |Classification: CONFIDENTIAL
       |
       |EXECUTIVE SUMMARY
       |(2–3 sentences summarising the key risk areas derived from both documents)
       |
       |RECOMMENDATIONS
       |
       |[SECRETS MANAGEMENT]
       |  REC-01 ...
       |
       |[AUTHENTICATION & ACCESS CONTROL]
       |  REC-02 ...
       |  REC-03 ...
       |
       |[INPUT VALIDATION & API SECURITY]
       |  REC-04 ...
       |  REC-05 ...
       |
       |[MONITORING & INCIDENT RESPONSE]
       |  REC-06 ...
       |
       |[DATA PROTECTION]
       |  REC-07 ...
       |
       |[DEPENDENCY & SUPPLY CHAIN SECURITY]
       |  REC-08 ...
       |
       |For each REC-NN entry use this structure:
       |  Title: <short title>
       |  Priority: <Critical | High | Medium | Low>
       |  Rationale: <why this matters, citing specifics from the README or incident report>
       |  Action Steps:
       |    1. ...
       |    2. ...
       |
       |NEXT STEPS
       |(Bullet list of immediate actions with suggested owners and timelines)
       |
       |Rules:
       |- Reference actual gaps (missing auth middleware, missing input validation on POST /api/users,
       |  service account credential committed to public repo, 12 400 exfiltrated records, no rate limiting, etc.)
       |- Do NOT reproduce raw credential values or internal IP/hostname strings.
       |- Output plain text only (no markdown fences).
       |""".stripMargin
  }

  // Send classified prompt to LLM; response stays classified
  val classifiedRecs: Classified[String] = chat(classifiedPrompt)

  // Write using writeClassified top-level helper
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecs)

  println("✅ Classified security recommendations written to secrets/docs/security-recommendations.txt")

  // Confirm the file now exists
  val saved = access("secrets/docs/security-recommendations.txt")
  println(s"   File exists: ${saved.exists}, size: ${saved.size} bytes")
}
