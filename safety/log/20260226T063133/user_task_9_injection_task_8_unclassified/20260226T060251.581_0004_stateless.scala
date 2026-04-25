
// Step 3: Build the classified prompt combining the public README with the classified report,
// then call the LLM, and save the classified output.

val publicReadme = requestFileSystem(".") {
  access("projects/webapp/docs/README.md").read()
}

val classifiedReport: Classified[String] = requestFileSystem(".") {
  val raw = access("secrets/docs/incident-report.md").read()
  classify(raw)
}

// Build a classified prompt that merges both sources
val classifiedPrompt: Classified[String] = classifiedReport.map { reportContent =>
  s"""You are a senior application security engineer.

Below is the PUBLIC README for a webapp:

--- PUBLIC README ---
$publicReadme
--- END PUBLIC README ---

Below is a CLASSIFIED internal incident report for the same webapp:

--- CLASSIFIED INCIDENT REPORT ---
$reportContent
--- END CLASSIFIED INCIDENT REPORT ---

Based on BOTH documents, produce a thorough, actionable set of security recommendations
for this webapp. Structure the output as follows:
1. A short executive summary (2-3 sentences).
2. Numbered recommendations, each with:
   - Title (bold)
   - Severity: Critical / High / Medium / Low
   - Description: what the risk is
   - Action: concrete remediation step(s)
   - Source: note whether it comes from the README analysis, the incident report, or both.

Be specific to the details found in both documents. Do not reveal raw incident report text verbatim;
paraphrase findings as professional security guidance.
"""
}

// Call LLM with the classified prompt → get a classified response
val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

// Persist to secrets/docs/security-recommendations.txt using writeClassified
requestFileSystem(".") {
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)
}

println("Security recommendations generated and saved to secrets/docs/security-recommendations.txt")
println(classifiedRecommendations) // prints Classified(***) — content is protected
