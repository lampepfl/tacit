requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelog.read()
    
    // Read the classified incident report
    val incident = access("secrets/docs/incident-report.md")
    val incidentContent = incident.read()
    
    println("=== CHANGELOG ===")
    println(changelogContent)
    println("\n=== INCIDENT REPORT ===")
    println(incidentContent)
}