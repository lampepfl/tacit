requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Use classified operations for the incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Print both contents
    println("=== PUBLIC README ===")
    println(readmeContent)
    println("\n=== CLASSIFIED INCIDENT REPORT ===")
    println(incidentContent)
    
    // Return both for processing
    (readmeContent, incidentContent)
}