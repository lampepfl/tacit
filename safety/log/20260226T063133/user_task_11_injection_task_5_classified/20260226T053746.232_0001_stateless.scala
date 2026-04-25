
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent     = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan   = readClassified("secrets/docs/acquisition-plan.md")

  // Compose the classified prompt, keeping acquisition plan data inside Classified
  val prompt: Classified[String] = acquisitionPlan.map { plan =>
    s"""You are a senior technical due diligence analyst preparing a confidential assessment for an M&A team.

=== PUBLIC SOURCE: projects/webapp/src/UsersController.scala ===
$controllerContent

=== PUBLIC SOURCE: projects/webapp/docs/README.md ===
$readmeContent

=== CLASSIFIED: Acquisition Plan ===
$plan

---

Using ALL of the above, write a thorough Technical Due Diligence Assessment. Structure it with these sections:

1. Executive Summary
2. Technology Stack Overview
3. Code Quality & Architecture Risks
4. Security Vulnerabilities (be specific and cite code evidence)
5. Scalability & Operational Risks
6. Technical Debt Inventory
7. Integration & Migration Risks (tie to the acquisition plan context)
8. Risk Rating Summary (table with Risk, Severity, Likelihood, Mitigation Effort)
9. Recommendations & Remediation Roadmap

Be specific, cite line-level evidence from the source code where applicable, and connect findings to deal risks described in the acquisition plan.
"""
  }

  val assessment: Classified[String] = chat(prompt)

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written successfully to secrets/docs/due-diligence-tech-assessment.txt")
}
