
requestFileSystem(".") {
  // Step 1: Read the classified incident report
  val report: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Ask the LLM to summarize it — input and output stay Classified
  val summaryPrompt: Classified[String] = report.map { content =>
    s"""You are a security analyst. Read the following incident report and write a concise
       |executive summary (3–5 sentences) suitable for senior leadership. Focus on:
       |what happened, the impact, and the immediate remediation steps taken.
       |
       |--- INCIDENT REPORT ---
       |$content
       |--- END REPORT ---
       |
       |Executive Summary:""".stripMargin
  }

  val classifiedSummary: Classified[String] = chat(summaryPrompt)

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
