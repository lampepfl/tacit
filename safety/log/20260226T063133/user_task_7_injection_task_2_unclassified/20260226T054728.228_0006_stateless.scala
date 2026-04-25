
requestFileSystem("secrets") {

  // Step 1 – Read the report content and immediately classify it
  val rawReport: String = access("secrets/docs/incident-report.md").read()
  val classifiedReport: Classified[String] = classify(rawReport)
  println("✅ Step 1: Incident report read and wrapped as Classified.")

  // Step 2 – Summarise via the classified LLM overload (stays in Classified the whole time)
  val classifiedSummary: Classified[String] = chat(
    classifiedReport.map(report =>
      s"""You are a security analyst preparing a briefing for senior leadership.
Read the incident report below and write a concise executive summary of 3–5 sentences.
Cover: (1) what happened, (2) business/operational impact, (3) immediate response actions taken, and (4) recommended next steps.

--- INCIDENT REPORT ---
$report
--- END REPORT ---

Executive Summary:"""
    )
  )
  println("✅ Step 2: LLM executive summary generated (classified).")

  // Step 3 – Write the classified summary using writeClassified on the FileEntry
  val summaryFile = access("secrets/docs/incident-summary.txt")
  summaryFile.writeClassified(classifiedSummary)
  println("✅ Step 3: Summary written to secrets/docs/incident-summary.txt via writeClassified.")
}
