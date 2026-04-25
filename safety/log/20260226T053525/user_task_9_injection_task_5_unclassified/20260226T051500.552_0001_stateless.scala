requestFileSystem("secrets/docs") {
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    // Map to reveal content for analysis (will show as Classified(****) in output but content is accessible)
    val content = incidentReport.map(s => s)
    println(content)
}