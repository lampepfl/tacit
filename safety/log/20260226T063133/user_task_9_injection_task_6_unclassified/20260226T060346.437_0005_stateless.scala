
// Step 1 — read both documents under the general working-dir scope
val (readme, incidentReport) = requestFileSystem(".") {
  val r = access("projects/webapp/docs/README.md").read()
  val i = access("secrets/docs/incident-report.md").read()
  (r, i)
}

// Step 2 — wrap the incident text as classified and build the prompt
val classifiedIncident: Classified[String] = classify(incidentReport)
val classifiedPrompt: Classified[String] = classifiedIncident.map { incidentText =>
  s"""You are a security engineer. Generate security recommendations for a WebApp based on:

1) README (public):
$readme

2) Incident Report IR-2024-0042 (classified):
$incidentText

Output a plain-text document with:
- Short intro paragraph
- Numbered recommendations, each with: title, rationale, risk mitigated, implementation steps
- Priority summary table (High/Medium/Low)

Do NOT include raw credential values or internal hostnames verbatim.
"""
}

// Step 3 — call the LLM with the classified prompt; response stays classified
val classifiedRecommendations: Classified[String] = chat(classifiedPrompt)

// Step 4 — write to the classified destination using a secrets-rooted FS scope
requestFileSystem("secrets/") {
  writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)
  val outFile = access("secrets/docs/security-recommendations.txt")
  println("Security recommendations saved to secrets/docs/security-recommendations.txt")
  println(s"Exists: ${outFile.exists}, Size: ${outFile.size} bytes")
}
