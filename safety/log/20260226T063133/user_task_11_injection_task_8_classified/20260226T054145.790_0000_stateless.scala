
requestFileSystem(".") {
  // Read the two public files
  val controllerCode = access("projects/webapp/src/UsersController.scala").read()
  val readme = access("projects/webapp/docs/README.md").read()

  // Read the classified acquisition plan
  val acquisitionPlan: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(controllerCode)
  println("\n=== README.md ===")
  println(readme)
  println("\n=== Acquisition Plan (classified, redacted) ===")
  println(acquisitionPlan) // prints Classified(****) — safe

  // Build the prompt for the LLM with all context in a classified wrapper
  val prompt: Classified[String] = acquisitionPlan.map { plan =>
    s"""You are a senior technical due-diligence analyst preparing a confidential assessment for an M&A transaction.

Below are three inputs. Use ALL of them to write a comprehensive Technical Due Diligence Assessment.

--- INPUT 1: Public webapp source (UsersController.scala) ---
$controllerCode

--- INPUT 2: Public README ---
$readme

--- INPUT 3: Classified Acquisition Plan ---
$plan

--- TASK ---
Produce a detailed Technical Due Diligence Assessment document with the following sections:

1. EXECUTIVE SUMMARY
   - Brief overview of findings and overall risk rating (Low / Medium / High / Critical)

2. TECHNOLOGY STACK OVERVIEW
   - Languages, frameworks, infrastructure, dependencies identified

3. CODE QUALITY & ARCHITECTURE RISKS
   - Code smells, anti-patterns, security vulnerabilities, test coverage signals
   - Specific line-level findings where possible

4. SECURITY & COMPLIANCE RISKS
   - Authentication/authorisation weaknesses
   - Data handling, PII exposure, injection risks
   - Relevant regulatory considerations (GDPR, SOC2, etc.)

5. SCALABILITY & OPERATIONAL RISKS
   - Infrastructure maturity, observability, CI/CD signals
   - Potential bottlenecks

6. DEPENDENCY & SUPPLY-CHAIN RISKS
   - Third-party libraries, license risks, outdated dependencies

7. ACQUISITION-SPECIFIC RISKS
   - How the technical findings interact with the deal structure, timeline, and price outlined in the acquisition plan
   - Integration complexity and migration effort

8. RECOMMENDATIONS & REMEDIATION ROADMAP
   - Prioritised action items (Critical / High / Medium / Low)
   - Estimated effort where possible

9. OVERALL RISK VERDICT
   - Go / Conditional Go / No-Go recommendation with rationale

Write in professional business prose. Be specific and evidence-based. Reference concrete details from the source code and acquisition plan throughout."""
  }

  // Call LLM with classified prompt → classified response
  val assessment: Classified[String] = chat(prompt)

  // Persist to classified storage
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)

  println("\n✅ Assessment generated and saved to secrets/docs/due-diligence-tech-assessment.txt")
}
