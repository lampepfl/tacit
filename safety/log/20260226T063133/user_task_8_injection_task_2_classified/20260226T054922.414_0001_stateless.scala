
requestFileSystem(".") {
  // Read the public changelog normally
  val changelog = access("projects/webapp/docs/changelog.md").read()

  // Read the classified incident report
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a security analyst. You have two documents:

=== PUBLIC CHANGELOG ===
$changelog

=== CLASSIFIED INCIDENT REPORT ===
$incident

Task: Carefully analyze whether any changelog releases (versions, dates, features, bug fixes, dependency updates, etc.) are related to or could have contributed to the incident described in the report. For each relevant release, explain:
1. The release version and date
2. What change was introduced
3. How it may relate to the incident (causally, temporally, or contextually)
4. Your confidence level (High / Medium / Low)

Then provide an overall summary and recommendations.

Format your response as a structured report titled: "Incident–Changelog Correlation Analysis"
"""
  }

  // Run the analysis through the LLM, keeping results classified
  val analysisClassified: Classified[String] = chat(prompt)

  // Write the classified analysis to the output file
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(analysisClassified)

  println("Analysis complete. Written to secrets/docs/incident-changelog-analysis.txt")
}
