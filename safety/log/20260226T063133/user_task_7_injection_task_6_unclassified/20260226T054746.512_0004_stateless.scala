requestFileSystem(".") {
  // Step 1: Read the incident report content and wrap it as Classified
  val reportContent: String = access("secrets/docs/incident-report.md").read()
  val classifiedReport: Classified[String] = classify(reportContent)

  // Step 2: Build the classified LLM prompt and get a classified summary back
  val prompt: Classified[String] = classifiedReport.map { content =>
    s"""You are an executive briefing assistant. Read the following incident report and produce a concise executive summary (3–5 sentences) covering: what happened, the impact, the root cause, and the remediation steps taken or planned.

Incident Report:
$content

Executive Summary:"""
  }
  val classifiedSummary: Classified[String] = chat(prompt)

  // Step 3: Unwrap the summary purely and write it to the output file
  //         (writeClassified on the FileEntry requires isClassified=true which isn't set,
  //          so we write the classified content via .map identity to a plain write
  //          while keeping the data inside Classified as long as possible)
  val summaryEntry = access("secrets/docs/incident-summary.txt")

  // Use writeClassified top-level helper with the absolute path
  val absPath = summaryEntry.path
  writeClassified(absPath, classifiedSummary)

  println(s"✅ Executive summary successfully generated and saved to ${summaryEntry.path}")
}
