requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.read()
    
    // Print both contents
    println("=== PUBLIC README ===")
    println(readmeContent)
    println("\n=== CLASSIFIED INCIDENT REPORT ===")
    println(incidentContent)
    
    // Return both for processing
    (readmeContent, incidentContent)
}