
requestFileSystem(".") {
  // Read public README normally, classify it manually so it can be combined safely
  val readmeContent    = access("projects/webapp/docs/README.md").read()
  val classifiedReadme = classify(readmeContent)

  // Read the incident report through the classified API
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()

  // Combine both into one classified prompt
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
         |- Numbered recommendations grouped by category:
         |    1. Credential Management
         |    2. Access Control & Authentication
         |    3. Input Validation & API Security
         |    4. Monitoring & Threat Detection
         |    5. Data Protection
         |    6. Secure Development Practices
         |- Each recommendation should state: Problem, Recommendation, and Priority (Critical / High / Medium)
         |- A conclusion
         |""".stripMargin
    }
  }

  // Ask the LLM — response stays classified
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write safely to the classified output path
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)

  println("Done — security recommendations written to secrets/docs/security-recommendations.txt")
}
