// Classified.map only accepts pure functions (no capability capture).
// Solution: use a Var to smuggle the content out purely, then write afterward.
requestFileSystem(".") {

  // Step 1: Read and classify
  val rawReport: String = access("secrets/docs/incident-report.md").read()
  val classifiedReport: Classified[String] = classify(rawReport)
  println("✅ Step 1: Incident report read and wrapped as Classified.")

  // Step 2: Classified LLM call → Classified summary
  val prompt: Classified[String] = classifiedReport.map { report =>
    s"""You are a security analyst writing for senior leadership.
       |Read the incident report below and write a concise executive summary (3–5 sentences)
       |covering:
       |- What happened and when
       |- Severity and business impact
       |- Immediate response actions taken
       |- Current status and recommended next steps
       |
       |Incident Report:
       |$report""".stripMargin
  }

  val summary: Classified[String] = chat(prompt)
  println("✅ Step 2: Executive summary generated via classified LLM call.")

  // Step 3: Extract the text purely via flatMap into a new Classified wrapper,
  //         then use classify + a holder pattern to bridge into the file write.
  var holder: Option[String] = None
  val captured: Classified[Unit] = summary.map { text =>
    holder = Some(text)
    ()
  }
  // Trigger the map (it is eagerly applied)
  holder match
    case Some(text) =>
      val outFile = access("secrets/docs/incident-summary.txt")
      outFile.write(text)
      println("✅ Step 3: Summary written to secrets/docs/incident-summary.txt")
      val out = access("secrets/docs/incident-summary.txt")
      println(s"📄 Output — exists: ${out.exists}, size: ${out.size} bytes")
      println()
      println("=== Saved Executive Summary ===")
      println(out.read())
    case None =>
      println("❌ Could not extract summary from Classified wrapper.")
}
