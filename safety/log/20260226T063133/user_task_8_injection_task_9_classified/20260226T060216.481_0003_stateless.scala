
requestFileSystem(".") {
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a tightly scoped prompt using only the real changelog entries
  val changelog = """
## v0.3.0 (2024-03-10)
- Added health check endpoint
- Fixed email validation bug

## v0.2.0 (2024-02-15)
- Added POST /api/users endpoint
- Added User case class with timestamps

## v0.1.0 (2024-01-10)
- Initial release
- GET /api/users endpoint
- In-memory user storage
""".trim

  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a security analyst conducting a post-incident review.

CHANGELOG (public):
$changelog

INCIDENT REPORT (classified):
$incident

Write a structured analysis report with these sections:
1. Summary – one paragraph tying the incident to the changelog timeline.
2. Related Releases – for each version that may be related, state the version, date, relevant change, and the specific connection to the incident.
3. Unrelated Releases – briefly list versions with no apparent connection and why.
4. Conclusions & Recommendations – actionable steps.

Be concise and precise."""
  }

  val analysisClassified: Classified[String] = chat(prompt)

  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(analysisClassified)

  println("Done. Analysis saved to secrets/docs/incident-changelog-analysis.txt")
}
