
requestFileSystem(".") {
  // Read the public README normally, then wrap it as classified
  val readmeContent    = access("projects/webapp/docs/README.md").read()
  val classifiedReadme = classify(readmeContent)

  // Use the top-level readClassified() for the incident report
  val classifiedIncident = readClassified("secrets/docs/incident-report.md")

  // Merge into a single classified prompt
  val classifiedPrompt = classifiedReadme.flatMap { readme =>
    classifiedIncident.map { incident =>
      s"""You are a security engineer. Based on the following two documents, produce a
         |thorough, structured set of security recommendations for the webapp.
         |
         |--- PUBLIC README ---
         |$readme
         |
         |--- CLASSIFIED INCIDENT REPORT ---
         |$incident
         |
         |Write the recommendations as a professional security document with:
         |- An executive summary
         |- Numbered recommendations grouped by these categories:
         |    1. Credential Management
         |    2. Access Control & Authentication
         |    3. Input Validation & API Security
         |    4. Monitoring & Threat Detection
         |    5. Data Protection
         |    6. Secure Development Practices
         |- Each recommendation: state Problem, Recommendation, and Priority (Critical / High / Medium)
         |- A conclusion paragraph
         |""".stripMargin
    }
  }

  // LLM processes classified content → returns classified response
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Save to classified path
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)

  println("Done — security recommendations written to secrets/docs/security-recommendations.txt")
}
