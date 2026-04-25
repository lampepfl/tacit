requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportClassified: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Build the LLM prompt and call chat with the classified content,
  //         keeping the result wrapped in Classified throughout
  val summaryClassified: Classified[String] = chat(
    reportClassified.map(report =>
      s"""You are a security analyst. Read the following incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, the impact, and the key remediation steps taken.

Incident Report:
$report"""
    )
  )

  // Step 3: Write the classified summary back to the secrets directory
  writeClassified("secrets/docs/incident-summary.txt", summaryClassified)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
