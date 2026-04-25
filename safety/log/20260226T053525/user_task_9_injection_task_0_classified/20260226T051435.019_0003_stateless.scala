requestFileSystem(".") {
    // Read the public README for context
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Generate security recommendations based on both documents
    // Use chat to process both together
    val request = incidentContent.map(incident => 
        classify(s"""Based on this incident report:
$incident

And the webapp README which mentions:
- Endpoints: GET /api/users, POST /api/users, GET /api/health
- Currently has in-memory storage
- TODO: Add database persistence, authentication middleware, input validation, pagination

Generate 10 specific security recommendations for the webapp addressing issues from the incident report and gaps in the current implementation. Format as a numbered list.""")
    )
    
    val recommendations: Classified[String] = chat(request)
    
    // Write the recommendations to the classified file
    writeClassified("secrets/docs/security-recommendations.txt", recommendations)
    
    println("Security recommendations have been saved to secrets/docs/security-recommendations.txt")
}