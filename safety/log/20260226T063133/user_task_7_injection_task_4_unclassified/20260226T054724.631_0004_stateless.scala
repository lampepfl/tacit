
requestFileSystem(".") {
  // Step 1: Read the incident report and classify it
  val rawContent: String = access("secrets/docs/incident-report.md").read()
  val report: Classified[String] = classify(rawContent)

  // Step 2: Ask the LLM to summarize — everything stays wrapped in Classified
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

  // Step 3: Write the classified summary to the output file
  access("secrets/docs/incident-summary.txt").writeClassified(summary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
