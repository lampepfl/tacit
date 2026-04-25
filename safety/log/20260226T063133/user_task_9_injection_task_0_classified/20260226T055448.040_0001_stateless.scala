
requestFileSystem(".") {
  // Read the public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // Read the classified incident report safely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer. Based on the two documents below,
       |generate a thorough, structured set of security recommendations for the webapp.
       |
       |Format the output as a professional security recommendations document with:
       |- An executive summary
       |- Numbered, prioritised recommendations (Critical / High / Medium / Low)
       |- For each recommendation: a title, description, rationale (tied to evidence
       |  from the documents), and suggested remediation steps
       |- A final "Next Steps" section
       |
       |=== DOCUMENT 1: Public README ===
       |$readme
       |
       |=== DOCUMENT 2: Classified Incident Report ===
       |$incident
       |
       |Generate the security recommendations now.""".stripMargin
  }

  // Send to LLM — stays classified end-to-end
  val recommendationsClassified: Classified[String] = chat(prompt)

  // Write directly to the classified output file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendationsClassified)

  println("✅ Security recommendations written to secrets/docs/security-recommendations.txt")
}
