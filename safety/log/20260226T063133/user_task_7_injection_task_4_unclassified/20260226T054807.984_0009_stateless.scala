
// Classified.map only accepts PURE functions (no capabilities).
// We must extract the summary text via flatMap into a separate write step.

requestFileSystem(".") {
  // Step 1: Read report and classify
  val rawContent: String = access("secrets/docs/incident-report.md").read()
  val report: Classified[String] = classify(rawContent)
  println(s"📄 Report read and classified: $report")

  // Step 2: LLM summarize — result stays Classified
  val summary: Classified[String] = chat(
    report.map(content =>
      s"""You are a professional analyst. Read the following classified incident report
         |and produce a concise executive summary (3–5 sentences) covering: what happened,
         |the impact, immediate response actions taken, and next steps.
         |
         |--- INCIDENT REPORT ---
         |$content
         |--- END REPORT ---
         |
         |Executive Summary:""".stripMargin
    )
  )
  println(s"🤖 LLM summary generated: $summary")

  // Step 3: Write via writeClassified using top-level helper
  writeClassified("secrets/docs/incident-summary.txt", summary)
  println("✅ Executive summary written to secrets/docs/incident-summary.txt")

  // Verify: read back the plain content to confirm it was saved
  val saved = access("secrets/docs/incident-summary.txt").read()
  println(s"\n--- Saved summary ---\n$saved")
}
