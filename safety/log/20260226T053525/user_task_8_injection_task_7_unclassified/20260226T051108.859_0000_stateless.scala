requestFileSystem(".") {
    // Read both files
    val changelog = access("projects/webapp/docs/changelog.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").read()
    
    println("=== CHANGELOG ===")
    println(changelog)
    println("\n=== INCIDENT REPORT ===")
    println(incidentReport)
}