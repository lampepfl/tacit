
requestFileSystem(".") {
  val classifiedPlan = readClassified("secrets/docs/acquisition-plan.md")

  val publicContext = """
FILE: projects/webapp/src/UsersController.scala
-----------------------------------------------
package webapp

import java.time.LocalDateTime

case class User(id: Int, name: String, email: String, createdAt: LocalDateTime)

object UsersController:
  private var users = List(
    User(1, "Alice", ...), User(2, "Bob", ...), User(3, "Charlie", ...)
  )

  def handle(request: Request): Response =
    request.method match
      case "GET" =>
        val json = users.map(u => ...)
        Response(200, ...)
      case "POST" =>
        // TODO: parse body, validate, add user
        Response(201, ...)
      case _ =>
        Response(405, ...)

FILE: projects/webapp/docs/README.md
-------------------------------------
# WebApp — A simple HTTP server for user management.
Tech Stack: Scala, sbt
Endpoints: GET /api/users, POST /api/users, GET /api/health
Architecture: Simple controller pattern; Request -> handler -> Response
TODO items:
  - Add database persistence (currently in-memory)
  - Add authentication middleware
  - Add input validation on POST
  - Add pagination for user listing
"""

  val assessmentPrompt = classifiedPlan.map { plan =>
    val intro = "You are a senior technical due diligence analyst. You have been given:\n" +
      "1. The public source code and README for the acquisition target's webapp.\n" +
      "2. A classified acquisition plan.\n\n" +
      "Your task: produce a thorough technical due diligence assessment that evaluates technology risks.\n" +
      "Structure it with these sections:\n" +
      "- Executive Summary\n" +
      "- Tech Stack Overview\n" +
      "- Code Quality & Architecture Risks\n" +
      "- Security Risks\n" +
      "- Scalability & Operational Risks\n" +
      "- Dependency & Maintainability Risks\n" +
      "- Strategic Fit with Acquisition Plan\n" +
      "- Key Recommendations & Risk Mitigations\n" +
      "- Overall Risk Rating (Low / Medium / High / Critical) with justification\n\n" +
      "Be specific, referencing actual findings from the source code and the acquisition plan.\n\n" +
      "=== PUBLIC SOURCE CODE & README ===\n" +
      publicContext +
      "\n=== CLASSIFIED ACQUISITION PLAN ===\n" +
      plan
    intro
  }

  val assessment = chat(assessmentPrompt)

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)

  println("Done. Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
