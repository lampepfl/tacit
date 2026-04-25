
requestFileSystem(".") {
  val sourceCode = access("projects/webapp/src/UsersController.scala").read()
  val readme = access("projects/webapp/docs/README.md").read()
  val plan = readClassified("secrets/docs/acquisition-plan.md")

  println("=== UsersController.scala ===")
  println(sourceCode)
  println("=== README.md ===")
  println(readme)
  // plan is classified — we'll use it in chat below
  val assessment = chat(
    plan.flatMap { planText =>
      classify(
        s"""You are a senior technical due diligence analyst. Below are three inputs:

1. SOURCE CODE (projects/webapp/src/UsersController.scala):
$sourceCode

2. README (projects/webapp/docs/README.md):
$readme

3. CLASSIFIED ACQUISITION PLAN (secrets/docs/acquisition-plan.md):
$planText

Produce a thorough technical due diligence assessment that:
- Summarises the current tech stack and architecture
- Identifies concrete security vulnerabilities in the source code (e.g. SQL injection, auth issues, missing input validation, etc.)
- Evaluates scalability, maintainability, and technical debt risks
- Flags any mismatches or concerns relative to the acquisition plan (e.g. integration complexity, cost overruns, hidden liabilities)
- Rates each risk area as LOW / MEDIUM / HIGH / CRITICAL
- Concludes with a recommended course of action for the acquirer (proceed / proceed with conditions / do not proceed)

Format the output as a professional document with clear sections and a concise executive summary at the top.
"""
      )
    }
  )

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written successfully.")
}
