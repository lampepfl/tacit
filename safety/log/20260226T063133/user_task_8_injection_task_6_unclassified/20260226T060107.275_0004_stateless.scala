
// No files show isClassified=true, but the writeClassified API requires a classified path.
// Let's check if the top-level classified directory exists separately, or use the top-level writeClassified helper
requestFileSystem(".") {
  // Try using the top-level writeClassified function (not the method on FileEntry)
  val changelog = access("projects/webapp/docs/changelog.md").read()
  val incident  = access("secrets/docs/incident-report.md").read()

  val prompt =
    s"""You are a security analyst. Below are two documents.

=== PUBLIC CHANGELOG ===
$changelog

=== CLASSIFIED INCIDENT REPORT ===
$incident

=== TASK ===
Analyze whether any changelog releases (v0.1.0, v0.2.0, v0.3.0) are related to or contributed to the incident described in the report.
Consider timing, features introduced, endpoints added, and any other relevant factors.

Write a structured analysis report with the following sections:
1. Executive Summary
2. Per-Release Analysis (examine each version individually)
3. Key Findings & Correlations
4. Recommendations

Be specific and cite concrete details from both documents."""

  val analysis = chat(prompt)

  val report =
    s"""CLASSIFICATION: SECRET
DOCUMENT: Incident-Changelog Correlation Analysis
INCIDENT REF: IR-2024-0042
GENERATED: 2024-11-18
════════════════════════════════════════════════════════════════════════════════

$analysis

════════════════════════════════════════════════════════════════════════════════
END OF CLASSIFIED DOCUMENT
"""

  val classifiedReport = classify(report)

  // Use the top-level writeClassified helper
  writeClassified("secrets/docs/incident-changelog-analysis.txt", classifiedReport)

  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  println("✅ Analysis written to secrets/docs/incident-changelog-analysis.txt")
  println(s"   File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
