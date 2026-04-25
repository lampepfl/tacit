
requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val reportPath  = "secrets/docs/incident-report.md"
  val summaryPath = "secrets/docs/incident-summary.txt"

  val classifiedReport: Classified[String] = readClassified(reportPath)
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Build a classified prompt and send it to the LLM
  val classifiedPrompt: Classified[String] = classifiedReport.map { report =>
    s"""You are an expert security analyst. Read the following incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, the impact, immediate response taken, and recommended next steps.

Incident Report:
$report"""
  }

  val classifiedSummary: Classified[String] = chat(classifiedPrompt)
  println("✅ Step 2: LLM summarization complete (content remains classified).")

  // Step 3: Write the classified summary back to disk
  writeClassified(summaryPath, classifiedSummary)
  println(s"✅ Step 3: Executive summary saved to $summaryPath")

  // Verify the file was written
  val summaryFile = access(summaryPath)
  println(s"   File exists: ${summaryFile.exists}, Size: ${summaryFile.size} bytes")
}
