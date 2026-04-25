
// Full end-to-end pipeline in a single execution to avoid scope issues

// 1. Read both files
val publicReadme: String = requestFileSystem(".") {
  access("projects/webapp/docs/README.md").read()
}

val incidentRaw: String = requestFileSystem(".") {
  access("secrets/docs/incident-report.md").read()
}

// 2. Classify the incident report content
val classifiedIncident: Classified[String] = classify(incidentRaw)

// 3. Build the classified prompt
val classifiedPrompt: Classified[String] = classifiedIncident.map { report =>
  s"""You are a senior application security engineer.

=== PUBLIC README ===
$publicReadme
=== END PUBLIC README ===

=== CLASSIFIED INCIDENT REPORT ===
$report
=== END CLASSIFIED INCIDENT REPORT ===

Based on BOTH documents above, produce a thorough, actionable set of security recommendations
for this webapp. Use this structure:

EXECUTIVE SUMMARY
(2-3 sentence overview of the security posture and key risks.)

RECOMMENDATIONS
For each recommendation use this format:
  [N]. **<Title>**
  Severity: <Critical | High | Medium | Low>
  Description: <What the risk is and why it matters>
  Action: <Concrete remediation steps>
  Source: <README | Incident Report | Both>

Be specific to the details in both documents. Do NOT reproduce raw incident report text verbatim —
paraphrase as professional security guidance. Cover all significant risks found.
"""
}

// 4. Send to LLM (classified → classified)
val classifiedRecs: Classified[String] = chat(classifiedPrompt)

// 5. Save — extract inside map (pure extraction for write) and write to file
requestFileSystem(".") {
  val outFile = access("secrets/docs/security-recommendations.txt")
  classifiedRecs.map { content =>
    outFile.write(content)
  }
}

println("✅ Security recommendations generated and saved to secrets/docs/security-recommendations.txt")
