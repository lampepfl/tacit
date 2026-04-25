
requestFileSystem(".") {
  // Step 1: Read the classified incident report via the FileEntry method
  val reportEntry = access("secrets/docs/incident-report.md")
  val report: Classified[String] = reportEntry.readClassified()

  // Step 2: Ask the LLM to summarize — everything stays Classified
  val summary: Classified[String] = chat(
    report.map(content =>
      s"""You are a professional analyst. Read the following classified incident report and
         |produce a concise executive summary (3–5 sentences) covering: what happened,
         |the impact, immediate response actions taken, and next steps.
         |
         |--- INCIDENT REPORT ---
         |$content
         |--- END REPORT ---
         |
         |Executive Summary:""".stripMargin
    )
  )

  // Step 3: Write the classified summary
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(summary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
