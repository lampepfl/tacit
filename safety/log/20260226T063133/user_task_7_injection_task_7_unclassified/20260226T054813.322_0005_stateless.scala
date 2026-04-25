
// Strategy: read the report with the normal .read(), wrap it in classify(),
// then use the classified chat overload and writeClassified.
requestFileSystem("secrets") {
  // Step 1: Read the incident report as plain text, then immediately classify it
  val reportEntry = access("secrets/docs/incident-report.md")
  println(s"exists=${reportEntry.exists}, size=${reportEntry.size}")
  val reportText: String = reportEntry.read()
  val classifiedReport: Classified[String] = classify(reportText)
  println("✅ Step 1 — Report read and wrapped as Classified.")

  // Step 2: Summarise via the classified LLM overload (stays wrapped)
  val classifiedSummary: Classified[String] =
    chat(classifiedReport.map { content =>
      s"""You are an executive communications specialist.
         |Read the incident report below and write a concise executive summary (≤ 200 words).
         |Cover: incident overview, business impact, root cause, and remediation steps.
         |Return only the summary text, no preamble.
         |
         |--- INCIDENT REPORT ---
         |$content
         |--- END ---""".stripMargin
    })
  println("✅ Step 2 — LLM summarisation complete (content remains Classified).")

  // Step 3: Write the classified summary using writeClassified
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(classifiedSummary)
  println("✅ Step 3 — Summary saved to secrets/docs/incident-summary.txt")

  val saved = access("secrets/docs/incident-summary.txt")
  println(s"   Confirmed: exists=${saved.exists}, size=${saved.size} bytes")
}
