// Analyse and print the comparison report
println("""
╔══════════════════════════════════════════════════════════════════════════════╗
║           API ENDPOINT AUDIT: README vs. Source Code                       ║
╠══════════════════════════════════════════════════════════════════════════════╣
║  Files examined                                                             ║
║  • projects/webapp/docs/README.md        (documentation)                   ║
║  • projects/webapp/src/Main.scala        (server + routing)                 ║
║  • projects/webapp/src/UsersController.scala  (request handling)           ║
╚══════════════════════════════════════════════════════════════════════════════╝

────────────────────────────────────────────────────────────────────────────────
 ENDPOINT COMPARISON TABLE
────────────────────────────────────────────────────────────────────────────────

  Endpoint                 Documented   Implemented   Status
  ─────────────────────── ──────────── ───────────── ───────────────────────────
  GET  /api/users          ✅ Yes       ✅ Yes         ✅ Both – fully consistent
  POST /api/users          ✅ Yes       ✅ Yes         ⚠️  Both – stub only (see below)
  GET  /api/health         ✅ Yes       ✅ Yes         ✅ Both – fully consistent
  PUT  /api/users          ❌ No        ❌ No          — Not documented, not implemented
  DELETE /api/users        ❌ No        ❌ No          — Not documented, not implemented

────────────────────────────────────────────────────────────────────────────────
 DETAILED FINDINGS
────────────────────────────────────────────────────────────────────────────────

1. GET /api/users
   • README says:  "List all users"
   • Main.scala:   server.addRoute("/api/users", UsersController.handle)
   • UsersCtrl:    case "GET" => builds JSON from in-memory users list, returns 200
   • Verdict:      ✅ Documented AND implemented. Behaviour matches description.

2. POST /api/users
   • README says:  "Create a new user"
   • Main.scala:   Handled by the same UsersController.handle (handles all methods)
   • UsersCtrl:    case "POST" => returns 201 {"status":"created"} — but the TODO
                   comment makes clear that body parsing, validation, and actual
                   insertion are NOT done. The user is never added.
   • Verdict:      ⚠️  Documented AND (partially) implemented, but the implementation
                   is a stub. The README description ("Create a new user") is
                   misleading; no user is actually created at runtime.

3. GET /api/health
   • README says:  "Health check"
   • Main.scala:   server.addRoute("/api/health", _ => Response(200, "OK"))
   • UsersCtrl:    (not involved — handled inline in Main.scala)
   • Verdict:      ✅ Documented AND implemented. Simple 200 OK response.

────────────────────────────────────────────────────────────────────────────────
 GAPS & OBSERVATIONS
────────────────────────────────────────────────────────────────────────────────

  • No undocumented routes exist — every route registered in Main.scala
    appears in the README. There is no implementation drift in that direction.

  • No documented routes are missing from the implementation — all three
    README entries have corresponding code.

  • The only discrepancy is qualitative: POST /api/users is documented as
    functional ("Create a new user") but is actually a TODO stub that always
    returns 201 without reading the request body, validating input, or
    storing anything. The README's own TODO section acknowledges
    "Add input validation on POST", but does not flag the missing core logic.

  • UsersController handles unknown HTTP methods with 405 Method Not Allowed,
    which is correct behaviour and consistent with only GET and POST being
    documented.

────────────────────────────────────────────────────────────────────────────────
 SUMMARY
────────────────────────────────────────────────────────────────────────────────

  Total documented endpoints  : 3
  Total implemented endpoints : 3
  Fully matching (doc + impl) : 2  (GET /api/users, GET /api/health)
  Documented but stub only    : 1  (POST /api/users — logic incomplete)
  Implemented but undocumented: 0
  Documented but not at all
    implemented               : 0
""")
