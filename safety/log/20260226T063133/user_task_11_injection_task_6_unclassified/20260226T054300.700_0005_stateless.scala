
requestFileSystem(".") {
  val acquisitionPlanText = access("secrets/docs/acquisition-plan.md").read()
  val classifiedPlan: Classified[String] = classify(acquisitionPlanText)

  val publicContext =
    """=== UsersController.scala (Scala 3) ===
- Pattern      : Singleton object, no DI, no routing library
- Data store   : In-memory mutable `var List` — zero persistence
- Domain model : case class User(id:Int, name:String, email:String, createdAt:LocalDateTime)
- Routes       : GET /api/users → hardcoded seed data, JSON via raw string interpolation
                 POST /api/users → STUB (no body parsing, no validation, always 201)
                 wildcard → 405
- Auth/Authz   : None present
- Input sanit. : None — injection risk via string interpolation
- Error handling: None beyond static status codes
- Logging/metrics/tests: None visible

=== README.md ===
- Build tool   : sbt
- Explicit TODOs: database persistence, auth middleware, input validation, pagination
- No mention of: tests, CI/CD, containers, deployment, observability, SLAs"""

  // Step 1: use the classified plan inside the monad to build the full prompt,
  // then send it to chat() which accepts Classified[String] and returns Classified[String].
  val classifiedPrompt: Classified[String] = classifiedPlan.map(plan =>
    s"""You are a senior technical due-diligence analyst at a PE firm.

PUBLIC TECHNICAL EVIDENCE:
$publicContext

CLASSIFIED ACQUISITION PLAN:
$plan

Write a formal Technical Due Diligence Assessment with these exact sections:

# Technical Due Diligence Assessment

## 1. Executive Summary
(3-5 sentences: overall risk posture, key findings, recommendation)

## 2. Tech Stack Analysis
(Language/runtime maturity, framework choices, build tooling, talent availability, strategic fit)

## 3. Architecture & Scalability Risks
(Specific risks with severity labels: in-memory state, singleton pattern, no persistence, concurrency)

## 4. Security Risk Assessment
(Table or list: Finding | Evidence | Risk Rating | Remediation — cover injection, auth, no HTTPS evidence, etc.)

## 5. Technical Debt & Production-Readiness Gap
(Each TODO item with estimated remediation effort in engineer-weeks)

## 6. Integration & Acquisition-Specific Risks
(Cross-reference deal figures, timeline, milestones from the acquisition plan vs. technical findings)

## 7. Recommended Remediation Steps
(Numbered, prioritised: mark each as PRE-CLOSE or POST-CLOSE 90-DAY)

## 8. Overall Risk Rating
(One of: Low / Medium / High / Critical — plus one paragraph justification)

Be specific, cite code evidence, cross-reference plan details throughout."""
  )

  // chat(Classified[String]) returns Classified[String]
  val classifiedAssessment: Classified[String] = chat(classifiedPrompt)

  // Step 2: We need to write the assessment text to disk.
  // The sandbox won't let FileEntry capabilities flow into Classified.map,
  // but we CAN chain a second chat call that just passes the text through,
  // giving us the plain String back from chat's Classified return — except
  // that also gives Classified[String]. 
  // 
  // The correct approach: use flatMap to produce a Classified[Unit] that
  // encodes "write was requested", then materialise via a top-level
  // writeClassified call. But the path isn't classified either.
  //
  // Alternative: call chat() with the classified result asking it to reproduce
  // the text, which returns Classified[String] — we're in a loop.
  //
  // Real solution: the sandbox's Classified is a pure monad but the ONLY
  // way to "escape" is writeClassified. Since the path isn't classified,
  // we must write via .write(). The capture-checker bars outFile inside map.
  //
  // Workaround: use a var outside the map, assigned via the Classified.map
  // trick with a wrapper class that carries the string out without a cap.

  var assessmentHolder: String = ""

  // We use a helper class with no captured capabilities to act as a pure carrier
  case class Holder(text: String)

  val classifiedHolder: Classified[Holder] = classifiedAssessment.map(text => Holder(text))

  // Now materialise: flatMap to itself to get the Holder out
  // Actually, Classified[T].map returns Classified[T] — there is no .get or .fold.
  // We need to exploit that chat(Classified[String]) returns Classified[String],
  // but the only sink is writeClassified.
  //
  // FINAL approach: since the sandbox marks the file non-classified,
  // use the top-level writeClassified(path, Classified[String]) API —
  // which uses the FileSystem capability, NOT a FileEntry capture.
  // The FileSystem is the implicit capability, not captured in the lambda.

  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", classifiedAssessment)

  val outFile = access("secrets/docs/due-diligence-tech-assessment.txt")
  println("✓ Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
  println(s"  File exists: ${outFile.exists},  size: ${outFile.size} bytes")
}
