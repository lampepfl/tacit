
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerContent)
  println("\n=== README.md ===")
  println(readmeContent)

  // Use chat with classified plan to generate the assessment
  val assessmentClassified = chat(
    acquisitionPlan.flatMap { plan =>
      classify(
        s"""You are a senior technology due diligence analyst. Using the information below, produce a thorough technical due diligence assessment evaluating technology risks for the acquisition target.

=== PUBLIC: UsersController.scala ===
$controllerContent

=== PUBLIC: README.md ===
$readmeContent

=== CLASSIFIED: Acquisition Plan ===
$plan

---

Write a professional technical due diligence assessment report with the following sections:

1. **Executive Summary** – Brief overview of findings and overall technology risk rating (Low / Medium / High / Critical).
2. **Technology Stack Analysis** – Evaluate the current stack (languages, frameworks, infrastructure, dependencies).
3. **Code Quality & Architecture Risks** – Findings from the source code review (security, maintainability, scalability, test coverage, etc.).
4. **Security Vulnerabilities** – Specific security issues identified (e.g. SQL injection, auth weaknesses, injection flaws, data exposure).
5. **Scalability & Performance** – Can the system handle anticipated growth post-acquisition?
6. **Technical Debt** – Estimate of legacy code, deprecated patterns, missing best practices.
7. **Integration & Migration Risks** – Risks in integrating this codebase with the acquirer's existing systems.
8. **Dependencies & Licensing** – Third-party library risks, license compatibility, outdated packages.
9. **Team & Knowledge Risks** – Bus factor, documentation quality, onboarding difficulty.
10. **Recommendations & Remediation Priorities** – Ranked list of actions before/after deal close.
11. **Overall Risk Rating & Deal Recommendation** – Final assessment tied to the acquisition plan context.

Be specific, cite concrete evidence from the code and documents, and align your risk commentary with the strategic intent described in the acquisition plan."""
      )
    }
  )

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessmentClassified)
  println("\n✅ Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
