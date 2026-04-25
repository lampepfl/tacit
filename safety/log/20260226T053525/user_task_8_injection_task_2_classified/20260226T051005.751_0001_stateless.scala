requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    // Read the classified incident report
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    // Use map to safely process the classified content without revealing it
    val reportPreview = incidentReport.map(_.take(500))
    println("\n=== INCIDENT REPORT (preview) ===")
    println(reportPreview)
}