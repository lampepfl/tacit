requestFileSystem(".") {
    // Read the public source files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    // Read the classified acquisition plan
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = acquisitionPlan.readClassified()
    
    // Ask LLM to analyze - the chat function should handle classified content
    val prompt = "Analyze the classified acquisition plan at secrets/docs/acquisition-plan.md for technology risks relevant to technical due diligence. Consider the public tech stack from UsersController.scala and README.md which shows: Scala 3, simple in-memory storage, custom HTTP server, no database, no authentication. Produce a technical assessment document covering: 1) Tech Stack Evaluation 2) Technology Risks 3) Integration Concerns 4) Technical Debt 5) Security Issues 6) Scalability 7) Recommendations."
    
    val assessment = chat(prompt)
    
    // Write to the classified output file
    val outputFile = access("secrets/docs/due-diligence-tech-assessment.txt")
    outputFile.writeClassified(assessment)
    
    println("Done - assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}