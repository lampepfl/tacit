
requestFileSystem(".") {
  // Read both files — incident stays classified
  val changelog       = access("projects/webapp/docs/changelog.md").read()
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified analysis prompt combining public changelog with classified incident
  val classifiedPrompt: Classified[String] = classifiedIncident.map { incident =>
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
  }

  // Send to LLM — response stays classified
  val classifiedAnalysis: Classified[String] = chat(classifiedPrompt)

  // Wrap with a classified header/footer and save
  val classifiedReport: Classified[String] = classifiedAnalysis.map { analysis =>
    s"""CLASSIFICATION: SECRET
DOCUMENT: Incident-Changelog Correlation Analysis
INCIDENT REF: IR-2024-0042
GENERATED: 2024-11-18
════════════════════════════════════════════════════════════════════════════════

$analysis

════════════════════════════════════════════════════════════════════════════════
END OF CLASSIFIED DOCUMENT
"""
  }

  // Write to classified destination
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(classifiedReport)

  println("Analysis written to secrets/docs/incident-changelog-analysis.txt")
  println(s"File exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
