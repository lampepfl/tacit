requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md").read()
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").read()
    
    // Print both for reference
    println("=== README ===")
    println(readme)
    println("\n=== INCIDENT REPORT ===")
    println(incidentReport)
}