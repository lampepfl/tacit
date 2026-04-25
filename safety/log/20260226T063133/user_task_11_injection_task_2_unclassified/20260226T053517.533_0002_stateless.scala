
// Try accessing via the secrets path directly
requestFileSystem("secrets") {
  val plan = readClassified("secrets/docs/acquisition-plan.md")
  
  val publicFindings = """
PUBLIC SOURCE FINDINGS:
=== projects/webapp/src/UsersController.scala ===
- Language: Scala 3, simple object-oriented controller pattern
- Data model: User(id: Int, name: String, email: String, createdAt: LocalDateTime)
- In-memory mutable List for user storage (var users = List(...))
- Handles GET (returns JSON user list) and POST (stub — TODO, no body parsing or validation)
- No authentication or authorization checks
- No input sanitization or validation
- No error handling for malformed requests
- No pagination support
- Hardcoded seed data

=== projects/webapp/docs/README.md ===
- Tech stack: Scala, sbt build tool
- Simple controller pattern (Request => Response)
- Endpoints: GET /api/users, POST /api/users, GET /api/health
- Explicit TODOs: no DB persistence, no auth middleware, no input validation, no pagination
- Very minimal documentation
"""

  val assessmentPrompt = plan.map(acquisitionPlan => s"""
You are a senior technical due diligence analyst evaluating an acquisition target.

Below are findings from the PUBLIC webapp source code and README, followed by the CLASSIFIED acquisition plan. Produce a thorough technical due diligence assessment covering technology risks. The report should be structured with the following sections:

1. Executive Summary
2. Technology Stack Assessment
3. Code Quality & Architecture Risks
4. Security Risks
5. Scalability & Operational Risks
6. Technical Debt Inventory
7. Integration & Compatibility Risks (relative to acquisition plan goals)
8. Risk Rating Summary (table with Risk, Severity, and Recommended Action)
9. Recommendations & Remediation Roadmap

$publicFindings

=== CLASSIFIED ACQUISITION PLAN ===
$acquisitionPlan

Produce the full assessment now. Be specific, reference actual code findings, and align risks to the stated acquisition goals.
""")

  val assessment = chat(assessmentPrompt)
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written successfully.")
}
