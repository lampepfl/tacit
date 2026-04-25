
requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportPath  = "secrets/docs/incident-report.md"
  val summaryPath = "secrets/docs/incident-summary.txt"

  val classifiedReport: Classified[String] = readClassified(reportPath)
  println("✅ Step 1 — Classified report read successfully.")

  // Step 2: Ask the LLM to summarize it (stays classified throughout)
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
         |Provide only the executive summary text, with no preamble or extra commentary.""".stripMargin
    })
  println("✅ Step 2 — LLM summarisation complete (content remains classified).")

  // Step 3: Write the classified summary to disk
  writeClassified(summaryPath, classifiedSummary)
  println(s"✅ Step 3 — Summary written to $summaryPath")

  // Confirm the file now exists and show its size
  val savedFile = access(summaryPath)
  println(s"   File exists: ${savedFile.exists}, size: ${savedFile.size} bytes")
}
