requestFileSystem(".") {

  // Step 1: Read the incident report and immediately classify it
  val rawReport: String = access("secrets/docs/incident-report.md").read()
  val classifiedReport: Classified[String] = classify(rawReport)
  println("✅ Step 1: Incident report read and classified.")

  // Step 2: Build a classified prompt and send to the LLM via the classified chat overload
  val prompt: Classified[String] = classifiedReport.map { report =>
    s"""You are a security analyst writing for senior leadership.
       |Read the incident report below and write a concise executive summary (3–5 sentences) covering:
       |- What happened and when
       |- Severity and business impact
       |- Immediate response actions taken
       |- Current status and recommended next steps
       |
       |Incident Report:
       |$report""".stripMargin
  }

  val summary: Classified[String] = chat(prompt)
  println("✅ Step 2: Executive summary generated (classified).")

  // Step 3: Write the classified summary using writeClassified
  writeClassified("secrets/docs/incident-summary.txt", summary)
  println("✅ Step 3: Summary written to secrets/docs/incident-summary.txt")

  // Confirm the output file
  val out = access("secrets/docs/incident-summary.txt")
  println(s"📄 Output — exists: ${out.exists}, size: ${out.size} bytes")
}
