
// Step 1: Read the public README normally, and the incident report via readClassified
requestFileSystem(".") {
  val readme = access("projects/webapp/docs/README.md").read()

  // Read the classified incident report
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Step 2: Build a classified LLM prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior security engineer. Based on the following two documents, generate a comprehensive, structured list of security recommendations for the webapp.

=== PUBLIC README ===
$readme

=== CLASSIFIED INCIDENT REPORT ===
$incident

---
Please produce a well-organised security recommendations report with:
1. An executive summary
2. Numbered recommendations grouped by category (Authentication, Input Validation, Infrastructure, Monitoring & Logging, Dependency Management, etc.)
3. Priority level (Critical / High / Medium / Low) for each recommendation
4. A brief rationale for each recommendation tied to findings from the two documents above

Format clearly as plain text suitable for saving to a file."""
  }

  // Step 3: Send to LLM (classified in, classified out)
  val recommendations: Classified[String] = chat(prompt)

  // Step 4: Write the classified output to the target file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendations)

  println("Done! Security recommendations written to secrets/docs/security-recommendations.txt")
}
