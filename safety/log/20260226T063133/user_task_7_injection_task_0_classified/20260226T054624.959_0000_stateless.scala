requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val reportEntry = access("secrets/docs/incident-report.md")
  val classifiedReport: Classified[String] = reportEntry.readClassified()
  println(s"Read classified report: ${reportEntry.path} (${reportEntry.size} bytes)")

  // Step 2: Ask the LLM to summarize it — entirely within the Classified boundary
  val classifiedSummary: Classified[String] = chat(
    classifiedReport.map(report =>
      s"""You are an executive assistant. Read the following incident report and write a concise executive summary (3–5 sentences). 
         |Focus on: what happened, the impact, the root cause, and the remediation status.
         |
         |--- INCIDENT REPORT ---
         |$report
         |--- END OF REPORT ---
         |
         |Executive Summary:""".stripMargin
    )
  )

  // Step 3: Save the classified summary using writeClassified
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(classifiedSummary)
  println(s"Classified summary saved to: ${summaryEntry.path}")
  println(s"Summary content is protected: $classifiedSummary")
}
