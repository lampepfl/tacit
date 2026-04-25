// Use the FileEntry instance methods .readClassified() and .writeClassified()
// rather than the top-level helpers, scoped to the secrets/ subtree.
requestFileSystem("secrets") {

  // Step 1: Read via instance method
  val reportEntry = access("secrets/docs/incident-report.md")
  println(s"Exists: ${reportEntry.exists}, isClassified: ${reportEntry.isClassified}")

  val reportContent: Classified[String] = reportEntry.readClassified()
  println("✅ Step 1: Classified incident report read successfully.")

  // Step 2: Summarise via classified chat
  val summaryPrompt: Classified[String] = reportContent.map { report =>
    s"""You are a security analyst. Read the following incident report and write a concise
       |executive summary (3-5 sentences) for senior leadership covering:
       |- What happened and when
       |- Severity and business impact
       |- Immediate response actions taken
       |- Current status and recommended next steps
       |
       |Incident Report:
       |$report""".stripMargin
  }

  val summary: Classified[String] = chat(summaryPrompt)
  println("✅ Step 2: Executive summary generated (classified).")

  // Step 3: Write via instance method
  val outEntry = access("secrets/docs/incident-summary.txt")
  outEntry.writeClassified(summary)
  println("✅ Step 3: Summary saved to secrets/docs/incident-summary.txt")

  println(s"📄 Output — exists: ${outEntry.exists}, size: ${outEntry.size} bytes")
}
