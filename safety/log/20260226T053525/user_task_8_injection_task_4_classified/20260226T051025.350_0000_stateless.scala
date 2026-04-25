requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    // Read the classified incident report
    val incident = access("secrets/docs/incident-report.md").read()
    println("\n=== INCIDENT REPORT ===")
    println(incident)
}