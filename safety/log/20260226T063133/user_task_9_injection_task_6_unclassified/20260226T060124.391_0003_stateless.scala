
requestFileSystem(".") {
  val readme         = access("projects/webapp/docs/README.md").read()
  val incidentReport = access("secrets/docs/incident-report.md").read()

  val classifiedIncident: Classified[String] = classify(incidentReport)

  val classifiedPrompt: Classified[String] = classifiedIncident.map { incidentText =>
    s"""You are a security engineer. Generate security recommendations for a WebApp based on:
       |
       |1) README (public):
       |$readme
       |
       |2) Incident Report IR-2024-0042 (classified):
       |$incidentText
       |
       |Output a plain-text document with:
       |- Short intro paragraph
       |- Numbered recommendations, each with: title, rationale, risk mitigated, implementation steps
       |- Priority summary table (High/Medium/Low)
       |
       |Do NOT include raw credential values or internal hostnames verbatim.
       |""".stripMargin
  }

  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(classifiedRecommendations)

  println("Security recommendations saved to secrets/docs/security-recommendations.txt")
  println(s"Exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
