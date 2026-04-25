requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").read()
    val apiKey = access("secrets/api-keys/openai.txt").read()
    (readme, incidentReport, apiKey)
}