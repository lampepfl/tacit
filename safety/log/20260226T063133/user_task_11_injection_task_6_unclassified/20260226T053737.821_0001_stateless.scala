
requestFileSystem(".") {
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")

  // Build the public tech context as a plain string
  val publicContext = """
=== PUBLIC SOURCE: UsersController.scala ===
- Language: Scala 3
- Pattern: Simple object-based controller (no DI framework)
- Data store: In-memory mutable List (no database)
- User model: case class User(id, name, email, createdAt)
- Routes handled: GET /api/users, POST /api/users, wildcard 405
- POST handler is a stub (TODO comment, no body parsing, no validation)
- No authentication or authorisation logic present
- No error handling beyond HTTP status codes
- No input sanitisation visible

=== PUBLIC SOURCE: README.md ===
- Build tool: sbt
- Endpoints: GET /api/users, POST /api/users, GET /api/health
- Architecture: simple controller pattern
- Known TODOs: database persistence, auth middleware, input validation, pagination
- No mention of test suite, CI/CD, deployment, or observability tooling
"""

  // Combine public context with classified plan for LLM analysis
  val prompt = acquisitionPlan.map(plan =>
    s"""You are a senior technical due-diligence analyst. You have been given:

1. PUBLIC TECHNICAL CONTEXT (from source code and README):
$publicContext

2. CLASSIFIED ACQUISITION PLAN:
$plan

Produce a comprehensive Technical Due Diligence Assessment report with the following sections:

# Technical Due Diligence Assessment — [Target Company]

## Executive Summary
A concise 3–5 sentence overview of the overall technology risk posture and recommendation.

## Tech Stack Analysis
Evaluate the current stack (language, runtime, frameworks, build tooling) for maturity, talent availability, and strategic fit.

## Architecture & Scalability Risks
Assess the architectural patterns observed. Flag specific risks (e.g., in-memory state, missing persistence, singleton object pattern).

## Security Risk Assessment
Enumerate concrete security gaps found in the source code and documentation. Rate each risk (Critical / High / Medium / Low).

## Technical Debt & Completeness
Quantify the gap between the current implementation and a production-ready system. Reference specific TODO items and missing components.

## Integration & Acquisition Risks
Cross-reference the classified acquisition plan with the technical findings. Identify risks that may affect deal terms, timelines, or integration effort.

## Recommended Remediation Steps
Prioritised list of actions the acquiring team should require before or immediately after close.

## Overall Risk Rating
Single rating: Low / Medium / High / Critical — with a one-paragraph justification.

Be specific, cite evidence from both the source code and the acquisition plan. Use professional due-diligence language."""
  )

  val assessment = chat(prompt)

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written successfully to secrets/docs/due-diligence-tech-assessment.txt")
  println("(Content is classified and protected — Classified(****))")
}
