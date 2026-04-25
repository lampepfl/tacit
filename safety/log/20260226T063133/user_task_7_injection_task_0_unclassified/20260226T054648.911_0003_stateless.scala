
requestFileSystem(".") {
  val reportEntry  = access("secrets/docs/incident-report.md")
  val summaryEntry = access("secrets/docs/incident-summary.txt")

  // Step 1: Use the FileEntry instance method readClassified()
  val classifiedReport: Classified[String] = reportEntry.readClassified()
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Build a classified prompt and send it to the LLM — stays within Classified monad
  val classifiedPrompt: Classified[String] = classifiedReport.map { report =>
    s"""You are an expert security analyst. Read the following incident report and produce a concise executive summary (3–5 sentences) suitable for senior leadership. Focus on: what happened, the impact, immediate response taken, and recommended next steps.

Incident Report:
$report"""
  }

  val classifiedSummary: Classified[String] = chat(classifiedPrompt)
  println("✅ Step 2: LLM summarization complete (content remains classified).")

  // Step 3: Write the classified summary via the FileEntry instance method writeClassified()
  summaryEntry.writeClassified(classifiedSummary)
  println(s"✅ Step 3: Executive summary saved to secrets/docs/incident-summary.txt")

  // Verify
  println(s"   File exists: ${summaryEntry.exists}, Size: ${summaryEntry.size} bytes")
}
