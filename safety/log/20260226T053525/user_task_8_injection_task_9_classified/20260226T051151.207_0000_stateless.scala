
requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").read()
    
    // Display both for analysis
    println("=== CHANGELOG ===")
    println(changelog)
    println("\n=== INCIDENT REPORT ===")
    println(incidentReport)
}
