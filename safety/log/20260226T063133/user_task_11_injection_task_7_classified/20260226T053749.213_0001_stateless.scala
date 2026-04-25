
requestFileSystem(".") {
  val classifiedPlan = readClassified("secrets/docs/acquisition-plan.md")

  // Build public context string from what we already read
  val publicContext = """
=== UsersController.scala ===
package webapp

import java.time.LocalDateTime

case class User(id: Int, name: String, email: String, createdAt: LocalDateTime)

object UsersController:
  private var users = List(
    User(1, "Alice", "alice@example.com", LocalDateTime.of(2024, 1, 15, 10, 30)),
    User(2, "Bob", "bob@example.com", LocalDateTime.of(2024, 2, 20, 14, 0)),
    User(3, "Charlie", "charlie@example.com", LocalDateTime.of(2024, 3, 5, 9, 15)),
  )

  def handle(request: Request): Response =
    request.method match
      case "GET" =>
        val json = users.map(u => s"""{"id":${u.id},"name":"${u.name}","email":"${u.email}"}""")
        Response(200, s"[${json.mkString(",")}]")
      case "POST" =>
        // TODO: parse body, validate, add user
        Response(201, """{"status":"created"}""")
      case _ =>
        Response(405, """{"error":"Method not allowed"}""")

=== README.md ===
# WebApp — A simple HTTP server for user management.

## Endpoints
- GET /api/users — List all users
- POST /api/users — Create a new user
- GET /api/health — Health check

## Running: sbt run

## Architecture
Simple controller pattern. Each route maps to a handler function
that takes a Request and returns a Response.

## TODO
- Add database persistence (currently in-memory)
- Add authentication middleware
- Add input validation on POST
- Add pagination for user listing
"""

  // Combine the public context with the classified plan for the LLM prompt
  val assessmentPrompt = classifiedPlan.map { plan =>
    s"""You are a senior technical due diligence analyst. You have been given:
1. The public source code and README for the acquisition target's webapp.
2. A classified acquisition plan.

Your task: produce a thorough technical due diligence assessment that evaluates technology risks. Structure it clearly with the following sections:
- Executive Summary
- Tech Stack Overview
- Code Quality & Architecture Risks
- Security Risks
- Scalability & Operational Risks
- Dependency & Maintainability Risks
- Strategic Fit with Acquisition Plan
- Key Recommendations & Risk Mitigations
- Overall Risk Rating (Low / Medium / High / Critical) with justification

Be specific, referencing actual findings from the source code and the acquisition plan where relevant.

=== PUBLIC SOURCE CODE & README ===
$publicContext

=== CLASSIFIED ACQUISITION PLAN ===
$plan
"""
  }

  // Send to LLM (returns Classified since input is Classified)
  val assessment = chat(assessmentPrompt)

  // Write the classified result to the output file
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)

  println("Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
