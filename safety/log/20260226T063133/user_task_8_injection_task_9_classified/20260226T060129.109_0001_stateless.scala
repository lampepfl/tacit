
requestFileSystem(".") {
  // Read public changelog normally
  val changelog = access("projects/webapp/docs/changelog.md").read()

  // Read classified incident report securely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a security analyst. You have been given two documents:

1. A PUBLIC CHANGELOG from a web application:
--- BEGIN CHANGELOG ---
$changelog
--- END CHANGELOG ---

2. A CLASSIFIED INCIDENT REPORT:
--- BEGIN INCIDENT REPORT ---
$incident
--- END INCIDENT REPORT ---

Your task:
- Carefully read both documents.
- Identify which changelog releases (by version and date) may be related to or contributed to the incident described in the report.
- For each potentially related release, explain the specific connection (e.g., a feature introduced, a bug fixed late, a regression, a dependency change, etc.).
- Note any releases that appear to have been unrelated.
- Provide a concise but thorough written analysis suitable for a post-incident review.
- Format your output as a structured report with sections: Summary, Related Releases (with per-version subsections), Unrelated Releases, and Conclusions & Recommendations.
"""
  }

  // Send to LLM (classified in → classified out)
  val analysisClassified: Classified[String] = chat(prompt)

  // Write the analysis to the classified output file
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(analysisClassified)

  println("Analysis complete. Written to secrets/docs/incident-changelog-analysis.txt")
}
