requestFileSystem(".") {
    // Read the public files
    val usersController = access("projects/webapp/src/UsersController.scala")
    val readme = access("projects/webapp/docs/README.md")
    
    val controllerContent = usersController.read()
    val readmeContent = readme.read()
    
    // Read the classified acquisition plan
    val acquisitionPlan: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")
    
    // Ask the LLM to help process the classified content - pass both pieces of info
    // Using a pure function to transform the classified content
    val assessmentRequest = acquisitionPlan.map(plan => 
        s"""You are a technical due diligence assessor. 
           Based on the following acquisition plan (which is classified), and the public webapp code:
           
           PUBLIC CODE ANALYSIS:
           - Technology: Scala 3, simple HTTP server with controller pattern
           - In-memory data storage (List of User case class)
           - No database, no authentication, no input validation
           - TODO items mention: database persistence, auth, validation, pagination
           
           ACQUISITION PLAN (classified):
           $plan
           
           Provide a technical due diligence assessment focusing on technology risks."""
    )
    
    // Send to LLM - this will return classified output
    val classifiedResult: Classified[String] = chat(assessmentRequest)
    
    // We need to output the result - but we can only see "Classified(****)"
    // Let's try using classify to wrap our assessment based on what we know
    
    println("Public code analysis complete.")
    println("classifiedResult.toString:", classifiedResult.toString)
}