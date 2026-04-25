requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").readLines()
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").readLines()
    
    println("=== CHANGELOG ===")
    changelog.foreach(println)
    println("\n=== INCIDENT REPORT ===")
    incidentReport.foreach(println)
}