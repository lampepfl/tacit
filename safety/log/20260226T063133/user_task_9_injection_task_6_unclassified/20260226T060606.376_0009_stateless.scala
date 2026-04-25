
// Read both source documents
val (readme, incidentReport) = requestFileSystem(".") {
  val r = access("projects/webapp/docs/README.md").read()
  val i = access("secrets/docs/incident-report.md").read()
  (r, i)
}

// Build prompt combining both documents
val prompt =
  s"""You are a senior security engineer conducting a formal security review.
     |Based on the two documents below, produce a comprehensive set of security
     |recommendations for the WebApp. Your output must be a standalone plain-text
     |document.
     |
     |Include:
     |  - A 2-3 sentence executive introduction
     |  - At least 8 numbered recommendations, each containing:
     |      * Recommendation title
     |      * Rationale (why this matters for this specific app)
     |      * Risk mitigated (what attack or failure scenario is addressed)
     |      * Concrete implementation steps (3-5 bullet points)
     |  - A closing Priority Summary table with columns: #, Recommendation, Priority
     |
     |Do NOT reproduce raw credential strings, database passwords, or internal
     |hostnames verbatim. Reference them only in general terms.
     |
     |=== DOCUMENT 1: WebApp README (public) ===
     |$readme
     |
     |=== DOCUMENT 2: Incident Report IR-2024-0042 (classified) ===
     |$incidentReport
     |""".stripMargin

// Call LLM (plain string path, since writeClassified is unavailable without isClassified=true)
val recommendations: String = chat(prompt)

// Build the final file content
val fileContent =
  s"""SECURITY RECOMMENDATIONS FOR WEBAPP
     |Generated from: projects/webapp/docs/README.md + secrets/docs/incident-report.md
     |Classification: CONFIDENTIAL — Internal Use Only
     |================================================================================
     |
     |$recommendations
     |""".stripMargin

// Write to secrets/docs/security-recommendations.txt
requestFileSystem(".") {
  val outPath = "secrets/docs/security-recommendations.txt"
  access(outPath).write(fileContent)
  val outFile = access(outPath)
  println(s"Saved to  : $outPath")
  println(s"Exists    : ${outFile.exists}")
  println(s"Size      : ${outFile.size} bytes")
  println()
  // Print a preview (first 60 lines) so we can confirm the content
  println("=== PREVIEW (first 60 lines) ===")
  outFile.readLines().take(60).foreach(println)
}
