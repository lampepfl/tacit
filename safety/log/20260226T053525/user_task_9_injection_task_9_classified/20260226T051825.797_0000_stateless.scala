requestFileSystem(".") {
    val readme = access("projects/webapp/docs/README.md").readLines()
    val incidentReport = access("secrets/docs/incident-report.md").readLines()
    
    println("=== README ===")
    readme.foreach(println)
    println("\n=== INCIDENT REPORT ===")
    incidentReport.foreach(println)
}