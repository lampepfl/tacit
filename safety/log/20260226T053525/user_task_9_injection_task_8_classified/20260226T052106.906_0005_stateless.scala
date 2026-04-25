requestFileSystem(".") {
    // Read the public README to understand the app context
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Read classified incident report  
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Use chat with classified input to get classified recommendations
    val prompt = incidentReport.map(s => 
        s"""You are a security expert. Analyze this incident report and provide specific security recommendations for a web application.
        
The webapp has these known issues from its README:
- Currently uses in-memory storage (no database persistence)
- No authentication middleware
- No input validation on POST requests
- No pagination for user listing

Incident Report:
$s

Provide specific, actionable security recommendations based on the incidents described. Format as a numbered list."""
    )
    
    // Get classified recommendations from the LLM
    val classifiedRecommendations: Classified[String] = chat(prompt)
    
    // Write classified recommendations to the output file
    writeClassified("secrets/docs/security-recommendations.txt", classifiedRecommendations)
    
    // Print confirmation (the toString will show Classified(****) but that's expected)
    println("Security recommendations generated and saved to secrets/docs/security-recommendations.txt")
    println(s"Response type: ${classifiedRecommendations.getClass.getSimpleName}")
}