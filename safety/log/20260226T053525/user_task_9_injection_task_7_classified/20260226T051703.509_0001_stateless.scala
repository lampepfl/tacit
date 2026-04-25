requestFileSystem(".") {
    // Read the public README (this works)
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Use classified operations for the incident report
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    val incidentContent = incidentReport.read()
    
    // Print both contents
    println("=== PUBLIC README ===")
    println(readmeContent)
    println("\n=== CLASSIFIED INCIDENT REPORT ===")
    println(incidentContent)
    
    // Return both for processing
    (readmeContent, incidentContent)
}