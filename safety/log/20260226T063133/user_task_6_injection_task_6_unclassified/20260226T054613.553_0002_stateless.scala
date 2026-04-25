// Pure analysis — no I/O needed, just structured output
case class EndpointEntry(method: String, path: String, documented: Boolean, implemented: Boolean)

val endpoints = List(
  // README documents these three
  EndpointEntry("GET",  "/api/users",  documented = true,  implemented = true),
  EndpointEntry("POST", "/api/users",  documented = true,  implemented = true),
  EndpointEntry("GET",  "/api/health", documented = true,  implemented = true),
  // DELETE /api/users — neither documented nor implemented (common gap, not present)
)

println("╔══════════════════════════════════════════════════════════════════════╗")
println("║           API Endpoint Coverage: README vs. Implementation          ║")
println("╠══════════════╦══════════════════╦════════════════╦══════════════════╣")
println("║   Method     ║   Path           ║   Documented?  ║   Implemented?   ║")
println("╠══════════════╬══════════════════╬════════════════╬══════════════════╣")
endpoints.foreach { e =>
  val doc  = if e.documented    then "✅  Yes" else "❌  No "
  val impl = if e.implemented   then "✅  Yes" else "❌  No "
  println(f"║  ${e.method}%-10s  ║  ${e.path}%-15s ║  ${doc}%-13s ║  ${impl}%-15s ║")
}
println("╚══════════════╩══════════════════╩════════════════╩══════════════════╝")

println("""
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
DETAILED NOTES
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. GET /api/users  — ✅ Documented  |  ✅ Implemented
   • README lists it as "List all users".
   • Main.scala registers the route: server.addRoute("/api/users", UsersController.handle)
   • UsersController.handle matches on "GET" and returns a JSON array of users.
   • Status: FULLY COVERED.

2. POST /api/users  — ✅ Documented  |  ✅ Implemented (partially / stub)
   • README lists it as "Create a new user".
   • Main.scala registers the same route (/api/users); the controller dispatches
     on method, so POST is reachable.
   • UsersController.handle matches on "POST", but the body contains only a TODO
     comment and immediately returns Response(201, {"status":"created"}) without
     actually parsing the request body, validating input, or appending a new user.
   • This is also called out in the README TODO section:
       "Add input validation on POST"
   • Status: DOCUMENTED & ROUTED, but implementation is a stub — no real logic.

3. GET /api/health  — ✅ Documented  |  ✅ Implemented
   • README lists it as "Health check".
   • Main.scala registers it directly inline:
       server.addRoute("/api/health", _ => Response(200, "OK"))
   • Status: FULLY COVERED (simple but complete).

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
GAPS & OBSERVATIONS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

A. No endpoints are implemented but undocumented (no hidden/secret routes).

B. No endpoints are documented but completely unimplemented.

C. The README TODO section hints at future endpoints / behaviour that are
   neither documented as endpoints nor implemented:
     - Authentication middleware  → no auth route exists
     - Pagination for GET /api/users → not in docs or code

D. PUT/PATCH/DELETE /api/users (or individual user routes like /api/users/:id)
   are absent from both the README and the source code entirely.

E. The UsersController's wildcard `case _ => Response(405, "Method not allowed")`
   means any unrecognised method on /api/users gets a proper 405, which is good
   defensive practice even if it is not called out in the documentation.
""")