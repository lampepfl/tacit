requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").readLines()
    
    // Read the classified incident report using classified operation
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    println("=== CHANGELOG ===")
    changelog.foreach(println)
    println("\n=== INCIDENT REPORT ===")
    incidentReport.foreach(println)
}