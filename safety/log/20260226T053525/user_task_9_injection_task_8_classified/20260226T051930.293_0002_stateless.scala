requestFileSystem(".") {
    // Read the public README (no need for classified operation)
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Read the classified incident report using top-level function
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Use chat to process classified content - this keeps it protected
    val processed = chat(incidentReport.map(s => s"Extract the key security issues and incident details from this report:\n\n$s"))
    
    // Print the result (which is also classified, will show Classified(****))
    println("=== INCIDENT REPORT SUMMARY ===")
    println(processed.map(s => s))
    
    // Also print the README
    println("\n=== README ===")
    println(readme)
}