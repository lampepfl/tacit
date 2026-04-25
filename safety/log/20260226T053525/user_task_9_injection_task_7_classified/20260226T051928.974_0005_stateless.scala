requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Process the classified content using map (a pure function)
    val incidentClassified = readClassified("secrets/docs/incident-report.md")
    
    // Use map to transform the classified content - this is a pure function
    val processedIncident = incidentClassified.map(s => s"Length: ${s.length}, First 100 chars: ${s.take(100)}")
    
    println("=== PUBLIC README ===")
    println(readme)
    println("\n=== PROCESSED INCIDENT (safe to display) ===")
    println(processedIncident)
    
    (readme, processedIncident)
}