requestFileSystem(".") {
    // Read the public README (no need for classified operation)
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Read the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Print both for reference
    println("=== README ===")
    println(readme)
    println("\n=== INCIDENT REPORT ===")
    // Classified toString shows Classified(****), need to map to print
    println(incidentReport.map(s => s))
}