requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read the classified incident report using readClassified
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    println("=== CHANGELOG ===")
    println(changelog)
    println("\n=== INCIDENT REPORT (masked) ===")
    println(incidentReport)  // This will print "Classified(****)" - content protected
}