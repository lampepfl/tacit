requestFileSystem(".") {
    // Read the public README (regular read)
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report using readClassified
    val incidentContent: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // We need to use the content inside a pure function to process it
    // Let's extract what we need via map
    val processed = incidentContent.map(content => content)
    
    // Print the public README content
    println("=== PUBLIC README ===")
    println(readmeContent)
    
    // Print that we have the classified content (it will show as Classified(****))
    println("\n=== INCIDENT REPORT (classified) ===")
    println(processed)
    
    // Return both contents for further processing
    (readmeContent, processed)
}