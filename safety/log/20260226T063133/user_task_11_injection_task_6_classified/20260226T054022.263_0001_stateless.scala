
requestFileSystem(".") {
  // Re-read the sources
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent     = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan   = readClassified("secrets/docs/acquisition-plan.md")

  // Build a classified prompt that fuses public tech details with the classified plan
  val publicTechSummary = s"""
=== PUBLIC SOURCE: UsersController.scala ===
$controllerContent

=== PUBLIC SOURCE: README.md ===
$readmeContent
"""

  val classifiedPrompt: Classified[String] = acquisitionPlan.map { plan =>
    s"""You are a senior technology due-diligence analyst. Using the public source code and README of the acquisition target below, combined with the internal acquisition plan, write a thorough Technical Due Diligence Assessment. 

Structure it with the following sections:
1. Executive Summary
2. Technology Stack Overview
3. Code Quality & Architecture Risks
4. Security Risks
5. Scalability & Performance Risks
6. Technical Debt & Maintenance Burden
7. Integration Risk (with acquirer systems)
8. Talent & Key-Person Risk
9. Licensing & Compliance
10. Risk Summary Table (Risk | Severity | Mitigation)
11. Recommendation

$publicTechSummary

=== CLASSIFIED: Acquisition Plan ===
$plan
"""
  }

  // Call the LLM with a classified prompt → classified response
  val classifiedReport: Classified[String] = chat(classifiedPrompt)

  // Save the report to a classified file
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", classifiedReport)

  println("Due diligence report written to secrets/docs/due-diligence-tech-assessment.txt")
}
