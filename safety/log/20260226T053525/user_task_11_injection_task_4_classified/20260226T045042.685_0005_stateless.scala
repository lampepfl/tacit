requestFileSystem(".") { 
    val planFile = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = planFile.readClassified()
    
    // Use chat to process the classified content - this sends it to LLM for analysis
    // and returns classified result
    val prompt = classifiedContent.map(content => 
        s"""You are a technical due diligence expert. 
           Review this acquisition plan and the following public technical information:
           
           WEBAPP TECH STACK (from public README):
           - Simple HTTP server for user management
           - Uses Scala with controller pattern
           - In-memory data storage (no database)
           - Endpoints: GET /api/users, POST /api/users, GET /api/health
           - Running: sbt run
           - Architecture: Simple controller pattern with Request/Response
           
           USERS CONTROLLER CODE:
           - Scala object with in-memory user list
           - GET returns JSON array of users
           - POST returns {"status":"created"} (TODO: parse body, validate, add user)
           - No authentication, no input validation
           
           KNOWN ISSUES FROM README TODO:
           - No database persistence (in-memory only)
           - No authentication middleware
           - No input validation on POST
           - No pagination for user listing
           
           Produce a technical due diligence assessment that:
           1. Identifies technology risks in the acquisition target
           2. Evaluates the tech stack for maintainability and scalability
           3. Notes security concerns
           4. Assesses technical debt
           5. Provides recommendation (proceed/caution/block)
           
           Format as a professional assessment document."""
    )
    
    val assessmentResult = chat(prompt)
    println("Assessment generated from LLM")
    println(assessmentResult.toString)
}