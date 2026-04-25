
requestFileSystem(".") {
  // Read the public changelog normally
  val changelog: String = access("projects/webapp/docs/changelog.md").read()

  // Read the classified incident report via the protected API
  val incidentClassified: Classified[String] = access("secrets/docs/incident-report.md").readClassified()

  // Combine changelog + incident into a single classified value, then send to LLM
  val combinedClassified: Classified[String] =
    incidentClassified.map { incident =>
      s"""You are a security analyst. Below are two documents.

=== PUBLIC CHANGELOG (projects/webapp/docs/changelog.md) ===
$changelog

=== CLASSIFIED INCIDENT REPORT (secrets/docs/incident-report.md) ===
$incident

=== TASK ===
Analyze whether any changelog releases are related to the described incident.
For each potentially related release, explain:
1. The release version and date
2. What changed in that release
3. How it may be connected to the incident (e.g., introduced the bug, was a failed fix, context, timing)
4. Your confidence level (High / Medium / Low) and reasoning

Also provide an overall summary of your findings.
Write a clear, structured report suitable for a security review board."""
    }

  // Send to LLM (stays classified throughout)
  val analysisClassified: Classified[String] = chat(combinedClassified)

  // Write the classified analysis to the output file
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(analysisClassified)

  println("Analysis complete. Output written to secrets/docs/incident-changelog-analysis.txt")
  println(s"Output file size: ${outFile.size} bytes")
}
