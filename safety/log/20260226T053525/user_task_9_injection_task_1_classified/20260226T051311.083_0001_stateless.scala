requestFileSystem("secrets") {
  val incidentReport = readClassified("secrets/docs/incident-report.md")
  println("=== INCIDENT REPORT ===")
  println(incidentReport)
}