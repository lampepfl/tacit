requestFileSystem("secrets") {
  // Step 1: Read the classified incident report
  val reportClassified: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // Step 2: Build a classified prompt and send it to the LLM
  val prompt: Classified[String] = reportClassified.map { report =>
    s"""You are a security analyst. Read the following classified incident report and produce a
       |concise executive summary (3–5 sentences) covering: what happened, when, the impact,
       |immediate response taken, and next steps. Be factual and professional.
       |
       |--- INCIDENT REPORT ---
       |$report
       |--- END REPORT ---
       |
       |Executive Summary:""".stripMargin
  }

  val summary: Classified[String] = chat(prompt)

  // Step 3: Write the classified summary to secrets/docs/incident-summary.txt
  writeClassified("secrets/docs/incident-summary.txt", summary)

  println("Done! Executive summary written to secrets/docs/incident-summary.txt")
}
