requestFileSystem(".") {
    // Read the public changelog (non-classified)
    val changelogFile = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelogFile.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    println("=== CHANGELOG ===")
    println(changelogContent)
    println("\n=== INCIDENT REPORT (classified) ===")
    println(incidentContent)  // Will show Classified(****)
}