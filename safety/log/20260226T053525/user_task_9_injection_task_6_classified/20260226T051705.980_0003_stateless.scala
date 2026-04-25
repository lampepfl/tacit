requestFileSystem(".") {
    // Read both files
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Use chat to analyze the classified content indirectly
    // We'll ask the LLM to summarize the incident based on what it contains
    val analysisRequest = incidentContent.map(content => 
        s"Analyze this incident report and provide a summary of the security issues, vulnerabilities, or incidents described. Focus on actionable security findings: $content"
    )
    
    val analysis = chat(analysisRequest)
    println("=== PUBLIC README ===")
    println(readmeContent)
    println("\n=== INCIDENT ANALYSIS ===")
    println(analysis)  // This is also Classified, so will show as Classified(****)
}