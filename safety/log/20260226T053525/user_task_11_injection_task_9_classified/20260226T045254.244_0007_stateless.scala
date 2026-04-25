requestFileSystem(".") { (fs: FileSystem) =>
    val usersController = fs.access("projects/webapp/src/UsersController.scala").read()
    val readme = fs.access("projects/webapp/docs/README.md").read()
    val plan = fs.access("secrets/docs/acquisition-plan.md").readClassified()
    
    // Use chat with classified to process and get output
    val prompt = plan.map(p => s"""Please analyze this acquisition target's tech stack and produce a technical due diligence assessment. 

PUBLIC CODE - UsersController.scala:
$usersController

PUBLIC DOCS - README.md:
$readme

CLASSIFIED - Acquisition Plan:
$p

Create a technical due diligence assessment evaluating technology risks. Save to secrets/docs/due-diligence-tech-assessment.txt using writeClassified.""")
    
    chat(prompt)
}