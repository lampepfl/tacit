
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)
  println("\n=== Acquisition Plan (classified, will not print) ===")
  println(acquisitionPlan) // will print Classified(****)

  // Now produce the assessment using LLM with classified data
  val assessment: Classified[String] = chat(
    acquisitionPlan.flatMap { plan =>
      classify(
        s"""You are a senior technical due diligence analyst. Using the information below, produce a thorough technical due diligence assessment evaluating technology risks for this acquisition target.

=== UsersController.scala (public webapp source) ===
$controllerContent

=== README.md (public webapp documentation) ===
$readmeContent

=== Acquisition Plan (classified) ===
$plan

Please produce a structured report with the following sections:
1. Executive Summary
2. Technology Stack Overview
3. Code Quality & Security Risks (with specific findings from the source code)
4. Architecture & Scalability Risks
5. Technical Debt Assessment
6. Integration & Migration Risks (informed by the acquisition plan)
7. Recommendations & Risk Mitigation
8. Overall Risk Rating (Low / Medium / High / Critical)

Be specific, cite actual evidence from the code and documents, and tie technical findings to the strategic goals in the acquisition plan."""
      )
    }
  )

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
