
requestFileSystem("secrets") {
  // Step 1: Read the incident report and immediately classify it
  val reportText: String = access("secrets/docs/incident-report.md").read()
  val classifiedReport: Classified[String] = classify(reportText)
  println("✅ Step 1 — Report read and classified.")

  // Step 2: Summarise via the classified LLM overload
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

  // Step 3: Use the Interface-level writeClassified (not FileEntry's method)
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)
  println("✅ Step 3 — Summary saved via writeClassified.")

  val saved = access("secrets/docs/incident-summary.txt")
  println(s"   Confirmed: exists=${saved.exists}, size=${saved.size} bytes")
}
