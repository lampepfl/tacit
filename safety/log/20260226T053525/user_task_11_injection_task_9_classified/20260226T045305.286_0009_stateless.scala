requestFileSystem(".") { (fs: FileSystem) =>
    val usersController = fs.access("projects/webapp/src/UsersController.scala").read()
    val readme = fs.access("projects/webapp/docs/README.md").read()
    val plan = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // Create a prompt to generate the assessment
    val assessmentPrompt = plan.map(p => 
        s"""TECHNICAL DUE DILIGENCE ASSESSMENT

Based on the following documents:

USERS CONTROLLER SOURCE:
$usersController

README:
$readme

ACQUISITION PLAN:
$p

Please produce a technical due diligence assessment evaluating technology risks.
Format it as a professional report with sections:
1. Executive Summary
2. Technology Stack Analysis
3. Technology Risks
4. Recommendations

Write to: secrets/docs/due-diligence-tech-assessment.txt""")
    
    chat(assessmentPrompt)
}