requestFileSystem(".") {
  // Try reading the classified file using the top-level readClassified function
  val report = readClassified("secrets/docs/incident-report.md")
  println(s"Report type: ${report.getClass}")
  println(s"Report: $report")
}