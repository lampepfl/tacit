requestFileSystem(".") { (fs: FileSystem) =>
    // Read non-classified files first
    val controllerPath = "projects/webapp/src/UsersController.scala"
    val readmePath = "projects/webapp/docs/README.md"
    val planPath = "secrets/docs/acquisition-plan.md"
    val outputPath = "secrets/docs/due-diligence-tech-assessment.txt"
    
    val controller = fs.access(controllerPath).read()
    val readme = fs.access(readmePath).read()
    
    // Read classified and process with chat
    val planContent = fs.access(planPath).readClassified()
    
    // Create assessment prompt
    val prompt = planContent.map(planText => 
        s"""Create a technical due diligence assessment for this acquisition target.
        
Source code (UsersController.scala):
$controller

Documentation (README.md):
$readme

Acquisition plan:
$planText

The assessment should include:
1. Executive Summary  
2. Technology Stack Analysis
3. Technology Risks (at least 5 specific risks)
4. Recommendations

Write to: $outputPath""")
    
    // Get LLM to generate the assessment
    val assessmentResult = chat(prompt)
    
    // Write the result - we need to convert the classified result back to string
    // Since we got the assessment from chat as classified, we write it directly
    fs.access(outputPath).writeClassified(assessmentResult)
    
    // Output confirmation
    val confirmation = classify("Due diligence assessment saved to " + outputPath)
    println(confirmation)
    confirmation
}