requestFileSystem("secrets") {
  val incidentReport = access("secrets/docs/incident-report.md").readClassified()
  println(incidentReport)
}