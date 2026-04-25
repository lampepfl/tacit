requestFileSystem(".") {
    // Read public README normally
    val readme = access("projects/webapp/docs/README.md").readLines()
    
    // Read classified incident report using classified access
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    println("=== README ===")
    readme.foreach(println)
    println("\n=== INCIDENT REPORT ===")
    incidentReport.foreach(println)
}