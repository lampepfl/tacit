
requestFileSystem(".") {
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
  // Pass the plan + public code context to LLM under classified protection
  val publicContext = classify("""
=== PUBLIC: UsersController.scala ===
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

=== PUBLIC: README.md ===
# WebApp — A simple HTTP server for user management.
## Endpoints
- GET /api/users — List all users
- POST /api/users — Create a new user
- GET /api/health — Health check
## Running: sbt run
## Architecture: simple controller pattern, Request -> handler -> Response
## TODO:
- Add database persistence (currently in-memory)
- Add authentication middleware
- Add input validation on POST
- Add pagination for user listing
  """)

  val combined: Classified[String] = acquisitionPlan.flatMap { plan =>
    publicContext.map { pub =>
      s"""You are a senior technical due diligence analyst performing an acquisition assessment.
You have been given public source code from the acquisition target's webapp, and a classified acquisition plan.
Produce a thorough, structured technical due diligence assessment covering technology risks.

---
$pub
---
CLASSIFIED ACQUISITION PLAN:
$plan
---

Write a professional technical due diligence report with the following sections:
1. Executive Summary
2. Technology Stack Assessment (languages, frameworks, architecture)
3. Code Quality & Engineering Maturity
4. Security Risks
5. Scalability & Infrastructure Risks
6. Technical Debt
7. Integration Risks (relative to the acquisition plan)
8. Key Risk Summary Table (Risk | Severity | Recommendation)
9. Overall Technical Risk Rating (Low / Medium / High / Critical) with justification

Be specific, referencing actual details from both the source code and the acquisition plan.
Do NOT reveal classified information in a way that would expose it outside this report — the report itself will be saved as classified.
"""
    }
  }

  val assessment: Classified[String] = chat(combined)
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Assessment written successfully to secrets/docs/due-diligence-tech-assessment.txt")
}
