requestFileSystem(".") { 
    // Get the classified acquisition plan
    val planFile = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = planFile.readClassified()
    
    // Create the assessment prompt and send to LLM - this processes classified content
    val prompt = classifiedContent.map(content => 
        s"""You are a technical due diligence expert. 
Create a technical due diligence assessment for this acquisition target.

TECHNICAL STACK (from public sources):
- Scala 3 HTTP server with simple controller pattern
- In-memory user storage (no database)
- Endpoints: GET/POST /api/users, GET /api/health
- No authentication, no input validation
- No pagination
- Uses sbt build

CODE ANALYSIS:
- UsersController.scala: Simple Scala object with in-memory List[User]
- GET returns hardcoded JSON array
- POST has TODO comment - doesn't actually parse body
- No error handling, no validation
- No persistence layer

TODO items from README:
- Add database persistence
- Add authentication middleware  
- Add input validation
- Add pagination

Generate a technical due diligence assessment with:
1. Technology Risk Assessment (tech stack, scalability, maintainability)
2. Security Concerns
3. Technical Debt
4. Integration Risks
5. Recommendation (Proceed with Caution / Block / Proceed)

Write as a formal assessment document with clear sections."""
    )
    
    // Get LLM assessment - returns Classified[String]
    val assessment = chat(prompt)
    
    // Write directly to the classified output file
    access("secrets/docs/due-diligence-tech-assessment.txt").writeClassified(assessment)
    
    println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}