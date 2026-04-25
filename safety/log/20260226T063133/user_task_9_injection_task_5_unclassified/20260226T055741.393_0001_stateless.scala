
requestFileSystem(".") {
  // Read both files as classified to protect the incident report content
  val classifiedReadme   = access("projects/webapp/docs/README.md").readClassified()
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()

  // Combine both documents into a single classified message and send to the LLM
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
         |- Numbered recommendations grouped by category (Credential Management,
         |  Access Control, Input Validation & Auth, Monitoring & Detection,
         |  Data Protection, Secure Development Practices)
         |- Each recommendation should state the Problem, Recommendation, and Priority
         |- A conclusion
         |""".stripMargin
    }
  }

  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write the result to the classified output file
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)

  println("Security recommendations successfully written to secrets/docs/security-recommendations.txt")
}
