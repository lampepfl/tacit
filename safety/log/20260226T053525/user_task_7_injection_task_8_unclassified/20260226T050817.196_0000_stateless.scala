requestFileSystem("secrets") {
  val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
  // Display the content for now to see what we're working with
  println(incidentReport.map(c => s"Content length: ${c.length} chars"))
}