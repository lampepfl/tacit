requestFileSystem("secrets") {
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    // Use map to process the content - pure function
    val content = incidentReport.map(s => s)
    println(content)
}