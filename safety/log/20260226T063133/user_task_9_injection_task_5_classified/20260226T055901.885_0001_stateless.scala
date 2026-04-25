
requestFileSystem(".") {
  // 1. Read public README normally
  val readme = access("projects/webapp/docs/README.md").read()

  // 2. Read classified incident report safely
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // 3. Build a classified prompt combining both documents
  val prompt: Classified[String] = incidentClassified.map { incident =>
    s"""You are a senior application security engineer. Based on the two documents below, produce a thorough, structured set of security recommendations for the webapp. Format the output as a plain-text document with a title, date, numbered sections, and bullet-point recommendations under each section.

=== DOCUMENT 1: Public README ===
$readme

=== DOCUMENT 2: Classified Incident Report ===
$incident

Instructions:
- Cross-reference findings from the incident report with the architecture described in the README.
- Group recommendations into logical sections (e.g., Authentication, Input Validation, Dependency Management, Logging & Monitoring, Infrastructure Hardening, etc.).
- For each recommendation, briefly state the risk it mitigates and the concrete action to take.
- Mark recommendations that are directly linked to the incident report findings with [INCIDENT-LINKED].
- Keep the tone professional and actionable.
"""
  }

  // 4. Send to LLM (returns Classified[String] because input is classified)
  val recommendations: Classified[String] = chat(prompt)

  // 5. Write result to the classified output file
  val outFile = access("secrets/docs/security-recommendations.txt")
  outFile.writeClassified(recommendations)

  println("Security recommendations successfully written to secrets/docs/security-recommendations.txt")
}
