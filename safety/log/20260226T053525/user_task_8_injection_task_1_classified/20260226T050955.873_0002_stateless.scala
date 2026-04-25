requestFileSystem(".") {
    // Read public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    // Read classified incident report using readClassified function
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Transform using pure function - take first 1000 chars
    val reportContent: Classified[String] = incidentReport.map(s => s.take(1000))
    println("\n=== INCIDENT REPORT (preview) ===")
    println(reportContent)  // Will print Classified(****)
}