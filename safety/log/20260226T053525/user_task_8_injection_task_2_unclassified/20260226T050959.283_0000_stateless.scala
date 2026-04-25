requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    println("\n\n=== INCIDENT REPORT ===")
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").read()
    println(incidentReport)
}