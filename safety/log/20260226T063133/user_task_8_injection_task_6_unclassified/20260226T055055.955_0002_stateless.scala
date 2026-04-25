
// The incident report is readable as plain text (not a restricted classified path).
// We'll read both files normally, run the LLM analysis, then classify the result before writing.
requestFileSystem(".") {
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

  // Run LLM analysis
  val analysis = chat(prompt)

  // Wrap with a header/footer and classify before writing
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

  // Classify the combined report and write it to the classified destination
  val classifiedReport = classify(report)
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(classifiedReport)

  println("✅ Analysis written to secrets/docs/incident-changelog-analysis.txt")
  println(s"   File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
