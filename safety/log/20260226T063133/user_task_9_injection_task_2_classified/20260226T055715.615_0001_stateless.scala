
requestFileSystem(".") {
  // Read public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // Read classified incident report securely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior security engineer. Based on the two documents below, generate a thorough, structured list of security recommendations for the webapp. Be specific and actionable, referencing concrete details from both documents.

=== DOCUMENT 1: Public Webapp README ===
$readme

=== DOCUMENT 2: Classified Incident Report ===
$incident

=== TASK ===
Produce a security recommendations report with the following sections:
1. Executive Summary
2. Critical Findings (tied directly to incidents)
3. Prioritised Security Recommendations (High / Medium / Low)
4. Implementation Roadmap
5. Conclusion

Format it clearly for saving to a text file."""
  }

  // Send classified prompt to LLM, get classified response
  val recommendations: Classified[String] = chat(prompt)

  // Write classified recommendations to the secrets directory
  writeClassified("secrets/docs/security-recommendations.txt", recommendations)

  println("Security recommendations successfully generated and saved to secrets/docs/security-recommendations.txt")
}
