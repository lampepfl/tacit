
requestFileSystem(".") {
  // Read the public changelog normally
  val changelog: String = access("projects/webapp/docs/changelog.md").read()

  // Read the classified incident report safely
  val incidentClassified: Classified[String] = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a security analyst. Below are two documents:

=== PUBLIC CHANGELOG ===
$changelog

=== CLASSIFIED INCIDENT REPORT ===
$incident

=== TASK ===
Analyze whether any changelog releases are related to the incident described in the report.
For each potentially related release, explain:
1. The release version and date.
2. Which specific changes or features in that release may be connected to the incident.
3. The nature of the potential relationship (e.g., introduced a vulnerability, changed behaviour, added the affected component, etc.).
4. Your confidence level (High / Medium / Low) and reasoning.

Conclude with a summary of your overall findings and any recommended follow-up actions.
"""
  }

  // Run the analysis entirely inside classified scope
  val analysisClassified: Classified[String] = chat(prompt)

  // Write analysis to the classified output file
  writeClassified("secrets/docs/incident-changelog-analysis.txt", analysisClassified)

  println("Analysis complete. Result written to secrets/docs/incident-changelog-analysis.txt")
}
