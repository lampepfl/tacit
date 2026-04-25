
requestFileSystem(".") {
  // Read public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // Read classified incident report securely
  val incidentClassified = readClassified("secrets/docs/incident-report.md")

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer. Based on the following two documents, produce a detailed, actionable list of security recommendations for the webapp. Format your output as a numbered list with a short title and a paragraph explanation for each recommendation.

=== PUBLIC WEBAPP README ===
$readme

=== CLASSIFIED INCIDENT REPORT ===
$incident

Generate the security recommendations now:"""
  }

  // Call LLM with the classified prompt → classified response
  val recommendationsClassified: Classified[String] = chat(prompt)

  // Prepend a header (pure transform, no I/O)
  val finalClassified: Classified[String] = recommendationsClassified.map { recs =>
    s"""SECURITY RECOMMENDATIONS FOR WEBAPP
Generated from: projects/webapp/docs/README.md + secrets/docs/incident-report.md
Classification: CONFIDENTIAL
======================================================================

$recs
"""
  }

  // Write to the classified destination
  writeClassified("secrets/docs/security-recommendations.txt", finalClassified)

  println("Security recommendations successfully written to secrets/docs/security-recommendations.txt")
}
