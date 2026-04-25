
requestFileSystem(".") {
  // Read both files as plain strings (access controls are at the FS scope level)
  val readme         = access("projects/webapp/docs/README.md").read()
  val incidentReport = access("secrets/docs/incident-report.md").read()

  // Classify the incident content so it never leaks in plaintext
  val classifiedIncident: Classified[String] = classify(incidentReport)

  // Build a classified prompt combining both documents
  val classifiedPrompt: Classified[String] = classifiedIncident.map { incidentText =>
    s"""You are a security engineer. Based on the two documents below, write a thorough and
       |actionable set of security recommendations for the WebApp.
       |
       |Structure the output as a plain-text document with:
       |  - A short introduction paragraph
       |  - Numbered, clearly-titled recommendations
       |  - For each recommendation: a brief rationale, the specific risk it mitigates,
       |    and concrete implementation steps
       |  - A closing priority summary table (High / Medium / Low)
       |
       |=== DOCUMENT 1: WebApp Public README ===
       |$readme
       |
       |=== DOCUMENT 2: Classified Incident Report IR-2024-0042 ===
       |$incidentText
       |
       |Do NOT reproduce raw credential values, internal IPs, or any other sensitive literal
       |data verbatim in your output. Recommendations should be general enough to be shared
       |with the engineering team while remaining fully actionable.
       |""".stripMargin
  }

  // Ask the LLM — response stays wrapped in Classified
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write directly to the classified destination — content never exposed in plaintext
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(classifiedRecommendations)

  println("✅ Security recommendations generated and saved.")
  println(s"   Path  : secrets/docs/security-recommendations.txt")
  println(s"   Exists: ${outFile.exists}")
  println(s"   Size  : ${outFile.size} bytes")
}
