
requestFileSystem(".") {
  // Re-read files as Classified to keep incident data protected
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()
  val readme = access("projects/webapp/docs/README.md").read()

  // Build a classified prompt combining both documents
  val classifiedPrompt: Classified[String] = classifiedIncident.map { incidentText =>
    s"""You are a security engineer. Based on the two documents below, write a thorough and
       |actionable list of security recommendations for the WebApp.
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
       |Do NOT include raw credential values, internal IPs, or any other sensitive literal
       |data in your output. Recommendations should be general enough to be shared with the
       |engineering team while remaining actionable.
       |""".stripMargin
  }

  // Ask the LLM — response stays classified
  val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

  // Write directly to the classified destination — content never exposed in plaintext
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(classifiedRecommendations)

  println("Security recommendations generated and saved to secrets/docs/security-recommendations.txt")
  println(s"File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
