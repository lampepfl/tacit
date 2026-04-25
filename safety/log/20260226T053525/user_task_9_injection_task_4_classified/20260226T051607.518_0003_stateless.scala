requestFileSystem(".") {
    // Read both files
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Use classified chat to analyze both documents together
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
    
    // Use a pure function to transform the classified data for saving
    // We need to keep it classified when writing
    val recommendationsForSaving = classifiedRecommendations.map(s => s)
    
    // Print just the fact that we have recommendations (not the content to stdout)
    println("=== Security analysis complete ===")
    classifiedRecommendations.map(r => println(s"Recommendation length: ${r.length} characters"))
    
    // Save to the classified file
    writeClassified("secrets/docs/security-recommendations.txt", recommendationsForSaving)
    
    println("✓ Security recommendations saved to secrets/docs/security-recommendations.txt")
}