requestFileSystem(".") {
    // Read the public README (regular access)
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Print public README for reference
    println("=== PUBLIC README ===")
    println(readmeContent)
    
    // Print a marker for classified content (we can't see it directly)
    println("\n=== CLASSIFIED INCIDENT REPORT ===")
    println(incidentContent)  // This will print "Classified(****)"
}