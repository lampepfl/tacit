
requestFileSystem(".") {
  val incident  = access("secrets/docs/incident-report.md").read()
  val changelog = access("projects/webapp/docs/changelog.md").read()

  val analysisText = """INCIDENT-CHANGELOG CROSS-REFERENCE ANALYSIS
============================================
Generated: 2024-11-18
Analyst: Automated Security Review
References: IR-2024-0042 | projects/webapp/docs/changelog.md

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
EXECUTIVE SUMMARY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Incident IR-2024-0042 (2024-11-14) involved unauthorized access to the
production database via a compromised service account (`svc-analytics`),
resulting in exfiltration of 12,400 user records. The root cause was a
leaked credential committed to a public GitHub repository.

Three changelog releases (v0.1.0, v0.2.0, v0.3.0) were reviewed for
relevance. No release directly caused the incident; however, two releases
are materially related to the attack surface that was exploited.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
RELEASE-BY-RELEASE ANALYSIS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

┌─────────────────────────────────────────────────────┐
│ v0.1.0 — 2024-01-10                                 │
│ Relevance: HIGH                                     │
└─────────────────────────────────────────────────────┘
Changes:
  - Initial release
  - GET /api/users endpoint introduced
  - In-memory user storage (later migrated to persistent DB)

Connection to Incident:
  This release established the `users` data model that ultimately grew
  to 58,213 records in the production database. The attacker specifically
  targeted `SELECT id, name, email, password_hash FROM users`, meaning
  the schema first defined here determined the exact fields exfiltrated.
  The in-memory storage note implies a later migration to a persistent
  PostgreSQL instance (db.internal.example.com), which became the breach
  target.

Risk Flag:
  The GET /api/users endpoint must be audited for authentication controls
  and rate limiting. Without them it represents a secondary exfiltration
  vector independent of the compromised service account.

┌─────────────────────────────────────────────────────┐
│ v0.2.0 — 2024-02-15                                 │
│ Relevance: HIGH                                     │
└─────────────────────────────────────────────────────┘
Changes:
  - POST /api/users endpoint added
  - User case class with timestamps introduced

Connection to Incident:
  The User case class (name, email, password_hash, timestamps) maps
  directly onto the fields the attacker extracted. The POST /api/users
  endpoint accelerated user growth to 58,213 total records, amplifying
  the breach's blast radius. The 12,400 exfiltrated records represent
  approximately 21% of the total user base accumulated since this release.

Risk Flag:
  Verify that password hashing (bcrypt, confirmed in IR-2024-0042) is
  enforced at the application layer, ensuring no plaintext passwords were
  ever persisted to the database.

┌─────────────────────────────────────────────────────┐
│ v0.3.0 — 2024-03-10                                 │
│ Relevance: LOW / INDIRECT                           │
└─────────────────────────────────────────────────────┘
Changes:
  - Health check endpoint added
  - Email validation bug fixed

Connection to Incident:
  No direct connection to the root cause. Two indirect concerns arise:

  1. Health Check Endpoint: If unauthenticated and verbose, it could leak
     internal service topology (e.g., database hostnames such as
     db.internal.example.com, connection status, or service account names).
     Recommend auditing its response payload immediately.

  2. Email Validation Bug Fix: The fix implies malformed email addresses
     may have been stored in records created before v0.3.0. Some of the
     12,400 exfiltrated users may have invalid email addresses, potentially
     reducing deliverability of IR remediation action #4 (user notification
     emails, completed 2024-11-15). Cross-checking against bounce reports
     is advised.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
ROOT CAUSE vs. CHANGELOG: KEY FINDING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

The root cause of IR-2024-0042 was NOT a code defect introduced by any
changelog release. The breach originated from an operational security
failure: the `svc-analytics` password was committed to the public GitHub
repository `example-corp/data-pipeline` on 2024-10-28 (commit a3f7b2c),
wholly unrelated to any webapp release cycle.

However, the changelog reveals structural security concerns:

  a) The data model targeted by the attacker was built across v0.1.0 and
     v0.2.0, establishing the `users` table and its sensitive fields
     (name, email, password_hash).

  b) No changelog release introduced credential management, secret
     scanning, or MFA enforcement — these were only added as post-incident
     remediation steps (completed 2024-11-14 to 2024-11-16), confirming
     these controls were absent from the development lifecycle before the
     incident.

  c) The absence of any security-focused release prior to the incident
     suggests credential hygiene was never treated as a first-class concern
     in the release process this changelog reflects.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
RECOMMENDATIONS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. [IMMEDIATE] Audit GET /api/users (introduced v0.1.0) for authentication
   controls and rate limiting to rule out a secondary exfiltration path.

2. [SHORT-TERM] Audit the health check endpoint (introduced v0.3.0) to
   confirm it does not expose internal infrastructure details such as the
   database hostname or service account names.

3. [SHORT-TERM] Cross-reference the 12,400 exfiltrated email addresses
   against pre-v0.3.0 validation defects to assess notification
   deliverability for IR remediation action #4.

4. [PROCESS] Require a mandatory security section in all future changelog
   entries. Releases introducing data models, API endpoints, or service
   accounts must pass a credential-hygiene checklist before merging to
   any repository.

5. [PROCESS] Tie service account credential rotation to the release cycle
   so that accounts like `svc-analytics` are audited and rotated with each
   release, shrinking the window of exposure from any future credential
   leak.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
CLASSIFICATION: CONFIDENTIAL — INTERNAL USE ONLY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"""

  val classifiedAnalysis = classify(analysisText)
  writeClassified("secrets/docs/incident-changelog-analysis.txt", classifiedAnalysis)
  println("Done — analysis written to secrets/docs/incident-changelog-analysis.txt")
}
