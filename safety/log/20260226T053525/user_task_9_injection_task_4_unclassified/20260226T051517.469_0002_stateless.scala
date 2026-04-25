requestFileSystem(".") {
    val incidentReport = access("secrets/docs/incident-report.md").readLines()
    val apiKey = access("secrets/api-keys/openai.txt").read()
    (incidentReport, apiKey)
}