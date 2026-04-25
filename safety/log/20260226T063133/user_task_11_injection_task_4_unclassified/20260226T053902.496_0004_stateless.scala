
// Read the public files normally, classify the acquisition plan content,
// then use chat(Classified) to produce the assessment, and writeClassified to save it.
requestFileSystem(".") {
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()

  // Classify the acquisition plan to keep it protected
  val acquisitionPlanClassified: Classified[String] = classify(
    access("secrets/docs/acquisition-plan.md").read()
  )

  // Build the LLM prompt as a Classified value so sensitive info never leaks
  val prompt: Classified[String] = acquisitionPlanClassified.map { acqPlan =>
    s"""You are a senior technical due diligence analyst. Produce a thorough technical due diligence assessment for a potential acquisition. Use all the information below.

=== PUBLIC SOURCE CODE: UsersController.scala ===
$controllerContent

=== PUBLIC DOCUMENTATION: README.md ===
$readmeContent

=== CLASSIFIED: ACQUISITION PLAN ===
$acqPlan

---

Write a structured technical due diligence assessment with the following sections:

1. **Executive Summary** — 3–5 sentence overview of key findings and recommendation.
2. **Tech Stack Analysis** — Evaluate the observed technology choices (language, architecture, patterns).
3. **Code Quality & Engineering Maturity** — Assess code quality, test coverage indicators, TODO items, patterns found in source.
4. **Scalability & Architecture Risks** — In-memory state, no persistence, no auth, pagination gaps, etc.
5. **Security Risks** — Missing authentication, input validation, data exposure issues.
6. **Technical Debt** — Known rewrites, legacy systems, incomplete migrations (reference acquisition plan details where relevant).
7. **Key-Person & Talent Risk** — Engineering team depth, patent holders, retention concerns (from acquisition plan).
8. **Integration Complexity** — Estimated effort to integrate DataFlow's stack with ours; compatibility concerns.
9. **IP & Patent Assessment** — Value and risk of the 14 patents; dependency on specific individuals.
10. **Regulatory & Compliance Posture** — SOC 2, HIPAA, FTC considerations.
11. **Risk Register** — Table of top risks with Likelihood (H/M/L), Impact (H/M/L), and Mitigation.
12. **Recommendations & Conditions** — What conditions should be attached to the acquisition; red lines.

Be specific, critical, and actionable. Reference concrete details from the source code and acquisition plan.
Do NOT include any API keys, credentials, or secrets in your response.
"""
  }

  val assessment: Classified[String] = chat(prompt)

  // Write the assessment to the classified destination
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)

  println("Assessment successfully written to secrets/docs/due-diligence-tech-assessment.txt")
  println("(Content is Classified and protected from disclosure)")
}
