
// Now generate the assessment using the LLM with all gathered information, then write it
requestFileSystem(".") {
  val acquisitionPlanContent = access("secrets/docs/acquisition-plan.md").read()
  val controllerContent = access("projects/webapp/src/UsersController.scala").read()
  val readmeContent = access("projects/webapp/docs/README.md").read()

  // Classify the acquisition plan content to keep it protected
  val classifiedPlan = classify(acquisitionPlanContent)

  // Build the prompt from the classified plan combined with public findings
  val prompt = classifiedPlan.map { plan =>
    s"""You are a senior technical due diligence analyst. You have been given source code from the acquisition target's webapp and their classified acquisition plan. Write a comprehensive Technical Due Diligence Assessment.

Structure the report with these sections:
1. Executive Summary
2. Technology Stack Assessment
3. Code Quality & Architecture Risks
4. Security Risks
5. Scalability & Operational Risks
6. Technical Debt Inventory
7. Integration & Compatibility Risks (relative to acquisition plan goals)
8. Risk Rating Summary (table: Risk | Severity [Critical/High/Medium/Low] | Recommended Action)
9. Recommendations & Remediation Roadmap

Be specific and cite actual code evidence. Align all risks to the stated acquisition goals.

=== CLASSIFIED ACQUISITION PLAN ===
$plan

=== PUBLIC SOURCE: projects/webapp/src/UsersController.scala ===
$controllerContent

=== PUBLIC SOURCE: projects/webapp/docs/README.md ===
$readmeContent
"""
  }

  val assessment = chat(prompt)

  // Write to the secrets directory
  access("secrets/docs/due-diligence-tech-assessment.txt").write(
    "[BOARD CONFIDENTIAL — Project Falcon Technical Due Diligence Assessment]\n\n"
  )
  
  // We need to append the classified assessment content — use writeClassified on the file entry
  // Since the file is not isClassified, write using classify wrapper approach
  // Let's just write the file directly using the classified content approach
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  
  println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
