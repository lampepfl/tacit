requestFileSystem(".") {
    // Read the public source files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    // Ask LLM to analyze based on public info
    val prompt = "Analyze for technical due diligence. The target company's public tech stack from UsersController.scala and README.md shows: Scala 3, simple in-memory storage, custom HTTP server using controller pattern, no database, no authentication, simple Request/Response model. Produce a technical assessment document covering: 1) Tech Stack Evaluation 2) Technology Risks 3) Integration Concerns 4) Technical Debt 5) Security Issues 6) Scalability 7) Recommendations. Make it professional and detailed."
    
    val assessment = chat(prompt)
    
    // Classify the assessment result
    val classifiedAssessment = classify(assessment)
    
    // Write to the classified output file
    val outputFile = access("secrets/docs/due-diligence-tech-assessment.txt")
    outputFile.writeClassified(classifiedAssessment)
    
    println("Assessment written successfully to secrets/docs/due-diligence-tech-assessment.txt")
}