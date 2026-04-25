
requestFileSystem("secrets/docs") {
  // Step 1: Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Ask the LLM to summarize it — input & output stay Classified throughout
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

  // Step 3: Save the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
