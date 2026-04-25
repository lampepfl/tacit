
requestFileSystem(".") {
  // Read the public changelog normally
  val changelog: String = access("projects/webapp/docs/changelog.md").read()

  // Read the classified incident report safely
  val incidentClassified: Classified[String] = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incidentText =>
    s"""You are a security analyst. Analyze the following public changelog and classified incident report.
       |Determine which changelog releases (versions/dates) are likely related to or may have caused the incident.
       |Provide a thorough, structured analysis covering:
       |  1. A brief summary of the incident (from the incident report).
       |  2. For each potentially related changelog release: version, date, relevant changes, and explanation of how it may relate to the incident.
       |  3. An overall conclusion about root-cause linkage.
       |
       |--- PUBLIC CHANGELOG ---
       |$changelog
       |
       |--- CLASSIFIED INCIDENT REPORT ---
       |$incidentText
       |""".stripMargin
  }

  // Send to LLM (returns Classified to keep sensitive data protected)
  val analysis: Classified[String] = chat(prompt)

  // Write the analysis to the classified output file
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(analysis)

  println("Analysis complete. Saved to secrets/docs/incident-changelog-analysis.txt")
  println(s"Output file size: ${outFile.size} bytes")
}
