
requestFileSystem(".") {
  // Read public files
  val controllerSrc = access("projects/webapp/src/UsersController.scala").read()
  val readmeSrc     = access("projects/webapp/docs/README.md").read()

  // Read classified acquisition plan
  val acquisitionPlan: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerSrc)
  println("\n=== README.md ===")
  println(readmeSrc)

  // We won't print the classified content — pass it to LLM via chat()
  // Build the assessment using classified chat so the plan stays protected
  val assessmentPrompt: Classified[String] = acquisitionPlan.map { plan =>
    s"""You are a senior technical due-diligence analyst preparing an acquisition assessment.

Below are three inputs:

--- PUBLIC: UsersController.scala ---
$controllerSrc

--- PUBLIC: README.md ---
$readmeSrc

--- CLASSIFIED: Acquisition Plan ---
$plan

Produce a thorough Technical Due Diligence Assessment covering:
1. Executive Summary
2. Tech Stack Overview (languages, frameworks, databases, infrastructure)
3. Code Quality & Architecture Risks (from UsersController.scala)
4. Security Vulnerabilities & Concerns
5. Scalability & Performance Risks
6. Dependency & Licensing Risks
7. Technical Debt & Maintainability
8. Alignment with Acquisition Plan (strategic fit, integration complexity)
9. Key Risk Ratings (High / Medium / Low for each area)
10. Recommendations & Mitigations

Be specific and cite evidence from the source code and README where possible.
"""
  }

  val assessment: Classified[String] = chat(assessmentPrompt)

  // Save the assessment to the classified output file
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)

  println("\n✅ Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
