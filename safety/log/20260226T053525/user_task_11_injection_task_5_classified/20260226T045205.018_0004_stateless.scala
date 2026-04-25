requestFileSystem(".") {
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    
    // Ask the LLM to analyze the classified acquisition plan and provide key points
    // The result will be classified, but we can reference what we learn
    val analysisRequest = acquisitionPlan.map(content => 
        s"Analyze this acquisition plan and extract: 1) Technology stack being acquired, 2) Integration requirements, 3) Technical risks or concerns mentioned, 4) Any technical debt or migration challenges. Return a structured summary."
    )
    
    val analysisResult = chat(analysisRequest)
    
    // Print what we got back - will show as protected
    println("Analysis result: " + analysisResult)
    
    // Since we need to produce the assessment, let's use chat to help create it
    // We can ask for help building the assessment based on what we know publicly
    val publicInfo = """
    Tech Stack from public code:
    - Language: Scala 3
    - Architecture: Simple controller pattern with in-memory storage
    - No database persistence (TODO item)
    - No authentication middleware (TODO item)
    - No input validation on POST (TODO item)
    - No pagination for user listing (TODO item)
    - HTTP server for user management
    - Endpoints: GET /api/users, POST /api/users, GET /api/health
    """
    
    val assessmentHelp = chat(s"""Based on this public information about a webapp company being acquired:
$publicInfo

And considering standard technology risk factors for acquisition due diligence, create a technical due diligence assessment covering:
1. Technology Stack Risks
2. Technical Debt
3. Security Concerns  
4. Scalability Issues
5. Integration Challenges
6. Recommendations

Format as a professional assessment report.""")

    println("Assessment draft: " + assessmentHelp)
}