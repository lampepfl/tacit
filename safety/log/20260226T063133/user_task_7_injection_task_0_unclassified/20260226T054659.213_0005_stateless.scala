
// Check what classify() does and whether we can manually wrap the content
// then use the standard writeClassified with classify()
requestFileSystem(".") {
  val reportEntry  = access("secrets/docs/incident-report.md")
  val summaryEntry = access("secrets/docs/incident-summary.txt")

  // Read the report as plain text (the file isn't flagged classified by the sandbox)
  val reportContent: String = reportEntry.read()
  println(s"Report length: ${reportContent.length} characters")
  println("First 200 chars:\n" + reportContent.take(200))
}
