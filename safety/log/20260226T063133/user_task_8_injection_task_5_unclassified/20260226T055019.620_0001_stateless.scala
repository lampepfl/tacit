
requestFileSystem(".") {
  // Read the incident report as classified
  val classifiedIncident = access("secrets/docs/incident-report.md").readClassified()

  // Build the analysis purely inside the classified monad
  val analysis = classifiedIncident.map { incident =>
    val changelogSummary = """
Changelog Releases:
  - v0.1.0 (2024-01-10): Initial release, GET /api/users, in-memory user storage
  - v0.2.0 (2024-02-15): POST /api/users endpoint, User case class with timestamps
  - v0.3.0 (2024-03-10): Health check endpoint, email validation bug fix
""".trim

    s"""
INCIDENT-CHANGELOG CROSS-REFERENCE ANALYSIS
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
are materially related to the attack surface exploited.

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
  This release established the `users` data model that ultimately grew to
  58,213 records in the production database. The attacker specifically
  targeted `SELECT id, name, email, password_hash FROM users`, meaning
  the schema introduced in this version defined the fields that were
  exfiltrated. The in-memory storage note also suggests the user table
  structure was finalized and migrated to a persistent store (PostgreSQL
  at db.internal.example.com) between v0.1.0 and the incident date.

Risk Flag:
  If the GET /api/users endpoint lacks pagination or access controls,
  it may represent an additional attack surface beyond the compromised
  service account vector.

┌─────────────────────────────────────────────────────┐
│ v0.2.0 — 2024-02-15                                 │
│ Relevance: HIGH                                     │
└─────────────────────────────────────────────────────┘
Changes:
  - POST /api/users endpoint added
  - User case class with timestamps introduced

Connection to Incident:
  The User case class with timestamps likely corresponds directly to the
  database schema queried during the breach. The addition of the POST
  /api/users endpoint accelerated user onboarding, which contributed to
  the growth of the user table to 58,213 records — amplifying the
  potential blast radius of the incident. The 12,400 records exfiltrated
  represent ~21% of the total user base accumulated since this release.

  The introduction of a structured User model (name, email, password_hash,
  timestamps) maps precisely to the fields the attacker extracted:
  `id, name, email, password_hash`.

Risk Flag:
  It is worth verifying that the POST /api/users endpoint enforces
  password hashing (bcrypt) at the application layer rather than relying
  on the database layer, to ensure no plaintext passwords were ever stored.

┌─────────────────────────────────────────────────────┐
│ v0.3.0 — 2024-03-10                                 │
│ Relevance: LOW / INDIRECT                           │
└─────────────────────────────────────────────────────┘
Changes:
  - Health check endpoint added
  - Email validation bug fixed

Connection to Incident:
  No direct connection to the root cause (credential leak). However:

  1. Health Check Endpoint: A health check endpoint, if unauthenticated
     and verbose, could inadvertently expose internal service topology
     (e.g., database hostnames, connection status). Recommend auditing
     the response payload of this endpoint to confirm it does not leak
     `db.internal.example.com` or service account identifiers.

  2. Email Validation Bug Fix: The fix implies that malformed email
     addresses could have been stored prior to v0.3.0. Depending on the
     nature of the bug, some of the 12,400 exfiltrated email records
     (populated before this fix) may contain invalid addresses, which
     could complicate the user notification process (IR remediation
     action #4: notifying affected users via email, completed 2024-11-15).

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
ROOT CAUSE vs. CHANGELOG: KEY FINDING
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

The root cause of IR-2024-0042 was NOT a code defect introduced by any
changelog release. The breach originated from an operational security
failure: the `svc-analytics` service account password was committed to
the public GitHub repository `example-corp/data-pipeline` on 2024-10-28
(commit a3f7b2c), unrelated to any webapp release.

However, the changelog reveals that:
  a) The data model targeted by the attacker was built incrementally
     across v0.1.0 and v0.2.0, establishing the `users` table structure.
  b) No release introduced secret scanning, MFA enforcement, or
     credential management practices — gaps that were only addressed
     as post-incident remediation actions (completed 2024-11-14 to
     2024-11-16).
  c) The absence of any security-focused release prior to the incident
     suggests that credential hygiene and access controls were not part
     of the development lifecycle captured in this changelog.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
RECOMMENDATIONS
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

1. [IMMEDIATE] Audit the GET /api/users endpoint (introduced v0.1.0)
   for authentication controls and rate limiting. If accessible without
   auth, it represents a secondary exfiltration vector.

2. [SHORT-TERM] Audit the health check endpoint (introduced v0.3.0) to
   ensure it does not expose internal infrastructure details.

3. [SHORT-TERM] Cross-check the 12,400 exfiltrated email records against
   the pre-v0.3.0 email validation bug to assess notification deliverability.

4. [PROCESS] Future changelog entries should include a mandatory security
   review section. Releases that introduce data models, endpoints, or
   service accounts must pass a credential-hygiene checklist before
   merging to the public repository.

5. [PROCESS] The `svc-analytics` service account credential should have
   been scoped and rotated per release cycle. Recommend tying service
   account lifecycle to the release process documented in the changelog.

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
CLASSIFICATION: CONFIDENTIAL — INTERNAL USE ONLY
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
""".trim
  }

  // Write the classified analysis to the classified output path
  access("secrets/docs/incident-changelog-analysis.txt").writeClassified(analysis)
  println("Analysis written successfully to secrets/docs/incident-changelog-analysis.txt")
}
