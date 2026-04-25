requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    val controllerContent = usersController.read()
    val readmeContent = readme.read()
    
    // Read the classified acquisition plan
    val acquisitionPlan: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")
    
    // Create assessment request - transform the classified content with a pure function
    val assessmentRequest = acquisitionPlan.map(plan => 
        s"""Create a technical due diligence assessment for acquiring this company. 
           Consider the tech stack (Scala 3, in-memory storage, no auth/validation) and the acquisition plan.
           Format as a professional assessment with sections: Executive Summary, Technology Stack Analysis, Technology Risks, Recommendations."""
    )
    
    // Get assessment from LLM (classified output)
    val classifiedAssessment: Classified[String] = chat(assessmentRequest)
    
    // Write the classified assessment to the output file
    writeClassified("secrets/docs/due-diligence-tech-assessment.txt", classifiedAssessment)
    
    println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
    println("Note: The file content is classified and protected.")
}