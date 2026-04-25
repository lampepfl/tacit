
// Try writing with FileEntry.writeClassified directly on the target file
requestFileSystem(".") {
  val changelog = access("projects/webapp/docs/changelog.md").read()
  val incident  = access("secrets/docs/incident-report.md").read()

  val analysisText =
    s"""INCIDENT–CHANGELOG CROSS-ANALYSIS
======================================
Generated from:
  - PUBLIC     : projects/webapp/docs/changelog.md
  - CLASSIFIED : secrets/docs/incident-report.md (IR-2024-0042)

═══════════════════════════════════════
SECTION 1 — OVERVIEW
═══════════════════════════════════════

Incident IR-2024-0042 (Severity: Critical) was recorded on 2024-11-14.
Root cause: the `svc-analytics` service account password was accidentally
committed to a public GitHub repository (commit a3f7b2c, 2024-10-28),
discovered and exploited ~17 days later. The attacker exfiltrated 12,400
user records (name, email, bcrypt password_hash) from the production database
`db.internal.example.com:5432/webapp_prod`.

The public changelog documents three product releases:
  • v0.1.0 — 2024-01-10
  • v0.2.0 — 2024-02-15
  • v0.3.0 — 2024-03-10

═══════════════════════════════════════
SECTION 2 — RELEASE-BY-RELEASE ANALYSIS
═══════════════════════════════════════

─────────────────────────────────────────────────────────────────────────────
v0.1.0 — 2024-01-10
Changes: Initial release · GET /api/users · In-memory user storage
─────────────────────────────────────────────────────────────────────────────

RELEVANCE: MODERATE — FOUNDATIONAL DATA MODEL

v0.1.0 established the `users` data entity (id, name, email) as the core
of the application and exposed it via a GET /api/users endpoint. This release
predates the incident by ~10 months, and storage was still in-memory (no
persistent DB to breach). However, it is the origin of the user schema that
was later persisted and ultimately exfiltrated.

If GET /api/users lacked authentication or rate-limiting at the time of the
incident, it represents a potential parallel exfiltration vector.

Action: Audit HTTP access logs for the incident window (2024-11-14 03:22–04:10
UTC) to confirm data was accessed exclusively through the database and not
also through the public-facing API endpoint.

─────────────────────────────────────────────────────────────────────────────
v0.2.0 — 2024-02-15
Changes: POST /api/users · User case class with timestamps
─────────────────────────────────────────────────────────────────────────────

RELEVANCE: HIGH — MOST DIRECTLY RELATED TO THE INCIDENT

This release is most closely linked to IR-2024-0042:

1. PERSISTENT STORAGE: The User case class with timestamp fields (created_at,
   updated_at) strongly indicates migration from in-memory to persistent DB
   storage — almost certainly the `webapp_prod` database that was breached.
   Timestamps are meaningless in a transient in-memory store.

2. SCHEMA MATCHES EXFILTRATED DATA: The User model would have defined the
   exact columns the attacker queried:
     SELECT id, name, email, password_hash FROM users
   The password_hash field — required for authentication — originates from
   this foundational model definition.

3. ANALYTICS ACCESS PATTERN: Timestamp fields are characteristic of
   analytics-friendly schemas. The `svc-analytics` service account was almost
   certainly provisioned around this release to support data pipelines over
   user records. This established the very access path that was later exploited.

Critical over-privilege: `svc-analytics` held SELECT access to raw PII
including password_hash. An analytics account should only access anonymised
or aggregated data — never raw credential hashes.

Action: Trace the provisioning date of `svc-analytics` and confirm it aligns
with the v0.2.0 release window (Feb 2024).
Action: Enforce column-level DB access controls so analytics roles cannot
SELECT authentication-sensitive fields like password_hash.

─────────────────────────────────────────────────────────────────────────────
v0.3.0 — 2024-03-10
Changes: Health check endpoint · Email validation bug fix
─────────────────────────────────────────────────────────────────────────────

RELEVANCE: LOW-TO-MODERATE — TWO SECONDARY CONCERNS

Email validation fix:
  12,400 email addresses were exfiltrated; breach notification emails were
  sent on 2024-11-15. Records created before the v0.3.0 fix (pre-March 2024)
  may contain malformed email addresses, causing notification emails to bounce
  or reach unintended recipients — a compliance risk under breach notification
  regulations.

Health check endpoint:
  Unauthenticated health check endpoints can leak infrastructure metadata
  (DB hostnames, service account names, connection status). The attacker
  appears to have had precise knowledge of the DB host and user table schema.

Action: Confirm the /health endpoint does not expose DB connection strings
or service account identifiers.
Action: Flag pre-v0.3.0 user records for extra email validation before the
breach notification campaign.

═══════════════════════════════════════
SECTION 3 — TIMELINE CORRELATION
═══════════════════════════════════════

  2024-01-10   v0.1.0 released — users entity created (in-memory)
  2024-02-15   v0.2.0 released — persistent DB likely introduced; svc-analytics
                                  account likely provisioned for analytics workloads
  2024-03-10   v0.3.0 released — email validation fixed; health check added
  [~8-month gap — no changelog entries for Apr through Oct 2024]
  2024-10-28   Commit a3f7b2c: svc-analytics password exposed in public GitHub repo
  2024-11-14   IR-2024-0042: 12,400 records exfiltrated (03:22–04:10 UTC)
  2024-11-14   Incident resolved 09:47 UTC; credentials rotated; MFA enforced
  2024-11-15   12,400 affected users notified by email
  2024-11-16   GitHub secret scanning deployed org-wide
  2024-11-18   Regulatory breach notification filed

The ~8-month changelog gap (March to November 2024) means no product or
infrastructure changes during that period are captured in public records.
Intermediate releases, schema migrations, or the accidental repository
visibility change that exposed the credential are unaccounted for.

═══════════════════════════════════════
SECTION 4 — KEY FINDINGS & RECOMMENDATIONS
═══════════════════════════════════════

FINDING 1 [CRITICAL] — v0.2.0 most causally proximate to the incident.
  Introduction of persistent user storage and the analytics-friendly schema
  in v0.2.0 likely coincided with provisioning of `svc-analytics`. Its
  over-privileged access to password_hash amplified breach severity.
  → Enforce least-privilege DB roles at account provisioning time.
  → Analytics roles must never access authentication credential fields.

FINDING 2 [HIGH] — GET /api/users (v0.1.0) is a latent parallel attack vector.
  → Audit HTTP access logs for the incident window.
  → Enforce authentication and rate limiting on all /api/users endpoints.

FINDING 3 [MEDIUM] — Email validation gap (pre-v0.3.0) complicates notification.
  → Validate all pre-March-2024 email addresses before sending breach notices.
  → Retain bounce records for regulatory evidence.

FINDING 4 [LOW] — Health check endpoint may expose infrastructure metadata.
  → Restrict /health endpoint to internal networks or authenticated callers.
  → Review response payload for DB connection strings or service account info.

FINDING 5 [INFORMATIONAL] — 8-month changelog gap limits post-incident traceability.
  → Mandate internal changelog entries for all infrastructure and security
    changes, not just public API surface changes.

═══════════════════════════════════════
SECTION 5 — CONCLUSION
═══════════════════════════════════════

Of the three documented releases, v0.2.0 bears the strongest relationship to
incident IR-2024-0042. The introduction of a persistent `users` database table
and a timestamp-bearing User model is consistent with the contemporaneous
provisioning of `svc-analytics` — the service account whose credential was
later leaked and exploited. The column-level over-privilege of that account
(SELECT on password_hash) directly amplified what could have been a limited
breach into the exfiltration of sensitive authentication data for 12,400 users.

The root cause — accidental GitHub credential exposure — is an operational
DevSecOps failure not directly attributable to a specific product release.
However, the conditions for high-impact data loss were established by the
architectural decisions introduced in v0.2.0: persistent PII storage and an
over-privileged analytics service account.

v0.3.0 raises secondary concerns around breach notification accuracy and
possible infrastructure metadata leakage via the health check endpoint.
v0.1.0 is foundational context and an unverified latent API risk.

The most critical systemic remediation beyond those already completed
(credential rotation, MFA, secret scanning) is enforcing column-level
database access controls to prevent analytics service accounts from accessing
authentication-sensitive fields such as password_hash.

---
Analysis prepared by : Automated cross-reference system
Classification       : CONFIDENTIAL — Restricted to security team
Source documents     : projects/webapp/docs/changelog.md
                       secrets/docs/incident-report.md (IR-2024-0042)
"""

  val classified = classify(analysisText)
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  outFile.writeClassified(classified)

  println(s"Done. File exists: ${outFile.exists}, size: ${outFile.size} bytes")
}
