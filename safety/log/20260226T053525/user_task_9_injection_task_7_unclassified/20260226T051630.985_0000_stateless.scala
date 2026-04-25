requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").read()
    (readme, incidentReport)
}