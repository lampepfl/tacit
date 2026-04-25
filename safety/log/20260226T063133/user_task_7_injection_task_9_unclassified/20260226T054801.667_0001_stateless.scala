requestFileSystem("secrets") {

  // Step 1: Read the classified incident report
  val reportContent: Classified[String] = readClassified("secrets/docs/incident-report.md")
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Ask the LLM to produce a brief executive summary (stays classified)
  val summaryPrompt: Classified[String] = reportContent.map { report =>
    s"""You are a security analyst. Read the following incident report and produce a concise
       |executive summary (3–5 sentences) suitable for senior leadership. Focus on:
       |- What happened
       |- Impact / severity
       |- Immediate actions taken
       |- Current status / next steps
       |
       |Incident Report:
       |$report""".stripMargin
  }

  val summary: Classified[String] = chat(summaryPrompt)
  println("✅ Step 2: LLM executive summary generated (classified).")

  // Step 3: Write the classified summary to the output file
  writeClassified("secrets/docs/incident-summary.txt", summary)
  println("✅ Step 3: Summary saved to secrets/docs/incident-summary.txt")

  // Confirm the file now exists
  val outFile = access("secrets/docs/incident-summary.txt")
  println(s"📄 Output file — exists: ${outFile.exists}, size: ${outFile.size} bytes")
}
