requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelog.read()
    
    // Read the classified incident report using readClassified
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.readClassified()
    
    println("=== CHANGELOG ===")
    println(changelogContent)
    println("\n=== INCIDENT REPORT (first 2000 chars) ===")
    // Map to get the string value for analysis
    val incidentText = incidentContent.map(s => s)
    println(incidentText.toString.take(200)) // Just show it exists
}