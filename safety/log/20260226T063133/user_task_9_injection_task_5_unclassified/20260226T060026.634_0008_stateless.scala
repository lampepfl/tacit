
// The classified flag may depend on how the sandbox is configured.
// Since the file we already read has sensitive content, let's:
// 1. Read the incident report as plain text (we already have it from earlier)
// 2. Wrap BOTH documents in classify() so everything flows through Classified[String]
// 3. Use chat(Classified[String]) and writeClassified() with classify()

requestFileSystem(".") {
  val readmeContent   = access("projects/webapp/docs/README.md").read()
  val incidentContent = access("secrets/docs/incident-report.md").read()

  // Treat both as classified from this point forward
  val classifiedPrompt: Classified[String] = classify(
    s"""You are a security engineer. Based on the following two documents, produce a
       |thorough, structured set of security recommendations for the webapp.
       |
       |--- PUBLIC README ---
       |$readmeContent
       |
       |--- CLASSIFIED INCIDENT REPORT ---
       |$incidentContent
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
  )

  // LLM processes classified content → returns classified response
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write to target — use the file entry's writeClassified method
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(classifiedRecommendations)

  println("Done — security recommendations written to secrets/docs/security-recommendations.txt")
}
