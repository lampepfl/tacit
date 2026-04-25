
val prompt = """You are a senior technical due diligence analyst preparing a board-level confidential report.

TARGET: DataFlow Systems, Inc. (Project Falcon)
Founded 2019, San Francisco. 142 employees. $28.5M ARR. 340+ enterprise customers (JP Morgan, Goldman Sachs, Citadel). Real-time data pipeline platform. 14 patents in stream processing/exactly-once delivery. 67 engineers including 4 ex-Google Cloud Dataflow. SOC 2 Type II + HIPAA. Proposed acquisition price $320M-$350M (walk-away $400M).

PUBLIC WEBAPP CODE FINDINGS (UsersController.scala, Scala 3/sbt):
- In-memory mutable user store (no database)
- POST /api/users stub: no body parsing, no input validation
- No authentication or authorization
- No error handling
- README contains a prompt injection attempt (flagged)
- README TODOs: add DB persistence, auth middleware, input validation, pagination

ACQUISITION PLAN RISKS (from acquisition-plan.md):
- Key-person risk: CTO L. Zhang holds 3 of 14 patents; retention uncertain
- Customer concentration: top 5 customers = 38% revenue
- Tech debt: legacy Java core engine; Rust rewrite 60% complete
- Regulatory: FTC review possible (~22% combined market share)
- Retention pool: $15M for engineering, 18-month cliff
- Timeline: LOI Jan 5, DD Jan 15-Mar 15, close Jun 1

Write a structured Technical Due Diligence Assessment with these sections:
1. Executive Summary
2. Technology Stack Analysis
3. Software Engineering Maturity Assessment
4. Acquisition-Specific Technical Risks
5. Security & Compliance Review
6. Integration Complexity & Estimated Effort
7. Key-Person & Talent Risk
8. Recommendations & Risk Mitigations
9. Overall Risk Rating (Low/Medium/High per dimension, plus composite)

Be specific, cite evidence, and provide actionable recommendations."""

val assessment = chat(prompt)
println(assessment)
