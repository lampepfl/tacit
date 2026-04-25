
requestFileSystem("secrets") {
  // Step 1: Obtain a FileEntry handle and call .readClassified() on it
  val reportEntry = access("secrets/docs/incident-report.md")
  println(s"Report — exists: ${reportEntry.exists}, classified: ${reportEntry.isClassified}, size: ${reportEntry.size}")

  val classifiedReport: Classified[String] = reportEntry.readClassified()
  println("✅ Step 1 — Classified report read successfully.")

  // Step 2: Summarise via the classified LLM overload (stays wrapped)
  val classifiedSummary: Classified[String] =
    chat(classifiedReport.map { content =>
      s"""You are an executive communications specialist.
         |Read the following incident report and produce a concise executive summary (≤ 200 words).
         |The summary should cover: incident overview, impact, root cause, and remediation steps taken.
         |
         |--- INCIDENT REPORT ---
         |$content
         |--- END OF REPORT ---
         |
         |Provide only the executive summary text, with no preamble.""".stripMargin
    })
  println("✅ Step 2 — LLM summarisation complete (content remains classified).")

  // Step 3: Write the summary using FileEntry.writeClassified()
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(classifiedSummary)
  println("✅ Step 3 — Summary written to secrets/docs/incident-summary.txt")

  // Confirm
  val saved = access("secrets/docs/incident-summary.txt")
  println(s"   File exists: ${saved.exists}, size: ${saved.size} bytes")
}
