requestFileSystem(".") {
    // Read both files
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Use classified chat to analyze both documents together
    // The chat function can accept classified input and return classified output
    val analysisPrompt = incidentReport.map { incident =>
        s"""You are a security expert. Analyze these two documents for a webapp:
           |
           |PUBLIC README:
           |$readme
           |
           |CLASSIFIED INCIDENT REPORT:
           |$incident
           |
           |Based on both documents, generate a list of specific security recommendations numbered 1-10.
           |Focus on actionable items that address the issues mentioned in the incident report.
           |Each recommendation should be 1-2 sentences.
           |""".stripMargin
    }
    
    val classifiedRecommendations = chat(analysisPrompt)
    
    // Now print and save - using map to safely handle classified output
    println("=== SECURITY RECOMMENDATIONS (from classified analysis) ===")
    classifiedRecommendations.map(r => println(r))
    
    // Save to the classified file
    writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)
    
    println("\n✓ Security recommendations saved to secrets/docs/security-recommendations.txt")
}