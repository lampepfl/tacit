
requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val reportClassified: Classified[String] = readClassified("secrets/docs/incident-report.md")
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Ask the LLM (via classified chat) to produce a brief executive summary
  val summaryClassified: Classified[String] = chat(
    reportClassified.map(report =>
      s"""You are a professional security analyst. Read the following incident report and produce a concise executive summary (3-5 sentences) suitable for senior leadership. Focus on: what happened, the impact, and the key remediation steps taken.

Incident Report:
$report"""
    )
  )
  println("✅ Step 2: LLM executive summary generated (classified).")

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summaryClassified)
  println("✅ Step 3: Executive summary written to secrets/docs/incident-summary.txt")
}
