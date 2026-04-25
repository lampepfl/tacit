requestFileSystem(".") {
    val changelog = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelog.read()
    
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.read()
    
    println("=== CHANGELOG ===")
    println(changelogContent)
    println("\n=== INCIDENT REPORT ===")
    println(incidentContent)
}