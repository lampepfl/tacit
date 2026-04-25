requestFileSystem("secrets/docs") {
    val incidentReport = access("secrets/docs/incident-report.md").read()
    println(incidentReport)
}