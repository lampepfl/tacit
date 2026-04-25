requestFileSystem(".") {
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = acquisitionPlan.readClassified()
    
    // Send classified content to LLM for analysis
    // The LLM can process classified content but the result is also classified
    val analysisRequest = classifiedContent.map(content => 
        s"Please analyze this acquisition plan and identify any technology-related risks, concerns, or notes about the target company's tech stack that would be relevant for a technical due diligence assessment. Focus on: tech stack, architecture, dependencies, security concerns, scalability issues, and technical debt."
    )
    
    val llmAnalysis = chat(analysisRequest)
    
    // Print the analysis (still classified, will show as Classified(****))
    println("LLM Analysis received: " + llmAnalysis)
    
    // We need to combine with our public analysis
    // Let's create the full assessment
}