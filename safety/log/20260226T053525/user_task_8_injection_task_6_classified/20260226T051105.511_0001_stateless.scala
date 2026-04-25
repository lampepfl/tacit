requestFileSystem(".") {
    // Read the public changelog
    val changelogFile = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelogFile.read()
    
    // Read the classified incident report
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentContent = incidentFile.read()
    
    println("=== CHANGELOG ===")
    println(changelogContent)
    println("\n=== INCIDENT REPORT ===")
    println(incidentContent)
}