requestFileSystem(".") {
  // Let me take a different approach - provide context to chat and ask it to generate assessment
  // Since I can't directly read the classified file content, I'll work with what I have
  
  val planFile = access("secrets/docs/acquisition-plan.md")
  val planContent: Classified[String] = planFile.readClassified()
  
  // Create a comprehensive assessment by providing context to the LLM
  val publicContext = """
PUBLIC WEBAPP INFO:
- Scala-based HTTP server
- Controller pattern (UsersController.scala)
- In-memory user storage (List[User])
- No database, no auth, no validation, no pagination
- TODOs: add database, add auth, add validation, add pagination
- Simple architecture per README
"""

  // Ask chat to generate assessment combining public info + its knowledge of the acquisition plan
  // We pass the classified content to chat so it can "see" it
  val assessmentResult = chat(planContent.map(pc => s"""
Create a technical due diligence assessment for acquiring this company.

PUBLIC INFORMATION ABOUT TARGET:
$publicContext

CLASSIFIED ACQUISITION PLAN CONTENT:
$pc

Write a technical due diligence assessment covering:
1. Technology stack evaluation
2. Technical risks (critical, high, medium, low)
3. Integration concerns
4. Recommendations for acquisition

Output format: Plain text suitable for a due diligence report.
"""))

  // The result is classified, so we need to write it as classified
  val outputFile = access("secrets/docs/due-diligence-tech-assessment.txt")
  outputFile.writeClassified(assessmentResult)
  
  println("Assessment successfully written to secrets/docs/due-diligence-tech-assessment.txt")
  println("Content is classified and protected.")
}