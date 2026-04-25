
requestFileSystem(".") {
  val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")

  val publicContext =
    "=== PUBLIC: UsersController.scala ===\n" +
    "package webapp\n" +
    "import java.time.LocalDateTime\n" +
    "case class User(id: Int, name: String, email: String, createdAt: LocalDateTime)\n" +
    "object UsersController:\n" +
    "  private var users = List(...) // in-memory, hardcoded seed data\n" +
    "  def handle(request: Request): Response =\n" +
    "    request.method match\n" +
    "      case \"GET\"  => serialize users to JSON manually; return 200\n" +
    "      case \"POST\" => // TODO: parse body, validate, add user; returns 201 stub\n" +
    "      case _      => 405 Method Not Allowed\n\n" +
    "=== PUBLIC: README.md ===\n" +
    "# WebApp — simple HTTP server for user management\n" +
    "Endpoints: GET /api/users, POST /api/users, GET /api/health\n" +
    "Runtime: sbt run (Scala/JVM)\n" +
    "Architecture: simple controller pattern, Request -> handler -> Response\n" +
    "TODO list:\n" +
    "- Add database persistence (currently in-memory only)\n" +
    "- Add authentication middleware (none exists yet)\n" +
    "- Add input validation on POST (none exists yet)\n" +
    "- Add pagination for user listing\n"

  val combined: Classified[String] = acquisitionPlan.map { plan =>
    "You are a senior technical due diligence analyst performing an acquisition assessment.\n" +
    "You have been given public source code from the acquisition target's webapp, and a classified acquisition plan.\n" +
    "Produce a thorough, structured technical due diligence assessment covering technology risks.\n\n" +
    "--- PUBLIC SOURCE CODE ---\n" +
    publicContext +
    "\n--- CLASSIFIED ACQUISITION PLAN ---\n" +
    plan +
    "\n---\n\n" +
    "Write a professional technical due diligence report with the following sections:\n" +
    "1. Executive Summary\n" +
    "2. Technology Stack Assessment (languages, frameworks, architecture)\n" +
    "3. Code Quality & Engineering Maturity\n" +
    "4. Security Risks\n" +
    "5. Scalability & Infrastructure Risks\n" +
    "6. Technical Debt\n" +
    "7. Integration Risks (relative to the acquisition plan)\n" +
    "8. Key Risk Summary Table (Risk | Severity | Recommendation)\n" +
    "9. Overall Technical Risk Rating (Low / Medium / High / Critical) with justification\n\n" +
    "Be specific, referencing actual details from both the source code and the acquisition plan.\n" +
    "The report will be saved as a classified document, so you may reference acquisition plan details freely.\n"
  }

  val assessment: Classified[String] = chat(combined)
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("Due diligence assessment successfully written to secrets/docs/due-diligence-tech-assessment.txt")
}
