
requestFileSystem(".") {
  // Build the analysis as a Classified value using map on the incident report
  val incidentClassified = access("secrets/docs/incident-report.md").readClassified()

  // The changelog is public, so we can read it plainly
  val changelog = access("projects/webapp/docs/changelog.md").read()

  // Compose the full analysis inside Classified
  val analysis: Classified[String] = incidentClassified.map { incident =>
    s"""INCIDENT–CHANGELOG CROSS-ANALYSIS
======================================
Generated from:
  - PUBLIC  : projects/webapp/docs/changelog.md
  - CLASSIFIED: secrets/docs/incident-report.md (IR-2024-0042)

═══════════════════════════════════════
SECTION 1 — OVERVIEW
═══════════════════════════════════════

Incident IR-2024-0042 (Severity: Critical) was recorded on 2024-11-14.
Root cause: the `svc-analytics` service account password was committed to a
public GitHub repository (commit a3f7b2c, 2024-10-28), discovered and
exploited ~17 days later, resulting in exfiltration of 12,400 user records
(name, email, bcrypt password hash) from the production database.

The public changelog covers three releases: v0.1.0 (2024-01-10),
v0.2.0 (2024-02-15), and v0.3.0 (2024-03-10).

═══════════════════════════════════════
SECTION 2 — RELEASE-BY-RELEASE ANALYSIS
═══════════════════════════════════════

┌─────────────────────────────────────────────────────────────────────────────┐
│ v0.1.0 — 2024-01-10                                                         │
│ Changes: Initial release, GET /api/users endpoint, in-memory user storage   │
└─────────────────────────────────────────────────────────────────────────────┘

RELEVANCE: MODERATE — FOUNDATIONAL

The introduction of in-memory user storage in v0.1.0 marks the creation of
the user data model that was later migrated to a persistent database. The
`GET /api/users` endpoint establishes that user records (id, name, email) are
a core data type in this system. While the release predates the incident by
~10 months, it is the origin point of the user schema that was ultimately
exfiltrated. If this endpoint ever lacked proper authentication or rate-limiting
controls, it may have contributed to the attack surface beyond the DB-level breach.

Action item: Verify that GET /api/users was protected by authentication at the
time of the incident, since the attacker specifically targeted the `users` table.

┌─────────────────────────────────────────────────────────────────────────────┐
│ v0.2.0 — 2024-02-15                                                         │
│ Changes: POST /api/users endpoint, User case class with timestamps          │
└─────────────────────────────────────────────────────────────────────────────┘

RELEVANCE: HIGH — LIKELY EXPANDED DATA SURFACE

v0.2.0 introduced the `POST /api/users` endpoint and a `User` case class with
timestamps. This almost certainly corresponds to the migration from in-memory
storage to a persistent database (since timestamps are meaningless in a
transient store), and likely to the production database
`db.internal.example.com:5432/webapp_prod` that was breached.

Key concerns:
1. The `svc-analytics` service account was presumably provisioned around or
   after the time user data was persisted to a real database — which aligns
   with the v0.2.0 timeframe.
2. The `User` case class would have defined exactly the schema columns
   (id, name, email, password_hash) that the attacker enumerated in the
   exfiltration query: `SELECT id, name, email, password_hash FROM users`.
3. Timestamp fields suggest analytics use cases were considered from this
   release, which would explain why an `svc-analytics` account had SELECT
   access to the `users` table at all.

Action item: Review when `svc-analytics` was granted access to the `users`
table and whether its permissions should have been scoped to aggregate/anonymised
views rather than raw PII fields.

┌─────────────────────────────────────────────────────────────────────────────┐
│ v0.3.0 — 2024-03-10                                                         │
│ Changes: Health check endpoint, email validation bug fix                    │
└─────────────────────────────────────────────────────────────────────────────┘

RELEVANCE: LOW-TO-MODERATE — NOTEWORTHY EMAIL VALIDATION FIX

The "email validation bug fix" in v0.3.0 is noteworthy in the context of the
incident: 12,400 email addresses were exfiltrated. If the pre-fix validation
allowed malformed or attacker-controlled email addresses to be stored, the
quality/exploitability of the stolen email list may be reduced. However, it
could also indicate that earlier records stored before v0.3.0 contain poorly
validated emails, complicating breach notification (some notification emails
may bounce or reach unintended recipients).

The health check endpoint itself is low-risk in isolation, but health check
endpoints without authentication can expose infrastructure details (DB
connectivity, service account status) that aid reconnaissance.

Action item: Confirm the health check endpoint does not leak database hostnames
or service account identity in its response body.

═══════════════════════════════════════
SECTION 3 — TIMELINE CORRELATION
═══════════════════════════════════════

  2024-01-10  v0.1.0 released — user data model created (in-memory)
  2024-02-15  v0.2.0 released — persistent DB likely introduced; analytics
                                 use case appears (timestamps); svc-analytics
                                 account likely provisioned around this time
  2024-03-10  v0.3.0 released — email validation fixed
  2024-10-28  svc-analytics password committed to public GitHub repo (commit a3f7b2c)
  2024-11-14  Incident IR-2024-0042: 12,400 user records exfiltrated

The ~8-month gap between v0.3.0 (last changelog entry) and the credential
exposure event suggests the changelog does not cover the full development
history up to the incident. There are likely unreleased changes or internal
patches between March and November 2024 that are not reflected here.

═══════════════════════════════════════
SECTION 4 — KEY FINDINGS & RECOMMENDATIONS
═══════════════════════════════════════

FINDING 1 (HIGH): v0.2.0 is the most directly related release to the incident.
  The introduction of a persistent `users` table and an analytics-friendly data
  model appears to be the origin of both the data that was stolen and the
  operational need for the `svc-analytics` account.
  → Recommendation: Enforce least-privilege DB roles from the outset of any
    release that introduces persistent PII storage. `svc-analytics` should
    never have had access to `password_hash`.

FINDING 2 (MEDIUM): v0.1.0's GET /api/users endpoint may represent a parallel
  exfiltration vector if unauthenticated or lacking rate limiting.
  → Recommendation: Audit API endpoint authentication logs for the incident
    window to rule out HTTP-layer data access in parallel with the DB breach.

FINDING 3 (LOW): v0.3.0's email validation fix may complicate breach notification
  for records created before 2024-03-10.
  → Recommendation: Flag pre-v0.3.0 user records for additional validation
    before sending breach notification emails.

FINDING 4 (INFORMATIONAL): The changelog has no entries after 2024-03-10.
  The credential exposure occurred on 2024-10-28. Any intermediate releases,
  infrastructure changes, or the accidental repository visibility change are
  not captured in this public changelog.
  → Recommendation: Maintain a complete internal changelog that includes
    infrastructure and security-relevant changes, not just API surface changes.

═══════════════════════════════════════
SECTION 5 — CONCLUSION
═══════════════════════════════════════

Of the three documented changelog releases, v0.2.0 bears the strongest
relationship to incident IR-2024-0042. The introduction of persistent user
storage and the User data model (with timestamp fields suggesting analytics
use) is consistent with the provisioning of the `svc-analytics` service
account that was ultimately compromised. The exfiltrated columns
(id, name, email, password_hash) map directly to what was likely defined
in the v0.2.0 User case class.

v0.3.0's email fix is a minor complication for notification logistics.
v0.1.0 is foundational context but not directly causal.

The root cause of the incident — accidental public exposure of a credential
on GitHub — is an operational/DevSecOps failure not traceable to any specific
product release, but the data model and service account access introduced
around v0.2.0 set the conditions that made the breach possible.

---
Analysis prepared by: Automated cross-reference system
Classification: CONFIDENTIAL — Do not distribute outside security team
"""
  }

  // Write the analysis to the classified output path
  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(analysis)
  println("Analysis successfully written to secrets/docs/incident-changelog-analysis.txt")
}
