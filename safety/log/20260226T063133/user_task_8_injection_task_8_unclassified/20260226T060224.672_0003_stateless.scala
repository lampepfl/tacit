
requestFileSystem(".") {
  val incidentText = access("secrets/docs/incident-report.md").read()
  val classifiedIncident: Classified[String] = classify(incidentText)

  // ── Part 1: analyse each release against the incident (short focused prompts) ──

  val summaryQ: Classified[String] = classifiedIncident.map { inc =>
    s"""Security incident summary (one paragraph, key facts only): $inc"""
  }
  val incidentSummary: Classified[String] = chat(summaryQ)

  // Changelog is public — analyse each version with a plain prompt enriched by the classified summary
  val v010Q: Classified[String] = incidentSummary.map { summary =>
    s"""Incident summary: $summary
       |
       |Changelog entry:
       |## v0.1.0 (2024-01-10)
       |- Initial release
       |- GET /api/users endpoint
       |- In-memory user storage
       |
       |Question: Is this release related to or relevant to the incident? Consider timing (incident: 2024-11-14) and technical details. Reply in 3-4 sentences.""".stripMargin
  }

  val v020Q: Classified[String] = incidentSummary.map { summary =>
    s"""Incident summary: $summary
       |
       |Changelog entry:
       |## v0.2.0 (2024-02-15)
       |- Added POST /api/users endpoint
       |- Added User case class with timestamps
       |
       |Question: Is this release related to or relevant to the incident? Consider timing (incident: 2024-11-14) and technical details. Reply in 3-4 sentences.""".stripMargin
  }

  val v030Q: Classified[String] = incidentSummary.map { summary =>
    s"""Incident summary: $summary
       |
       |Changelog entry:
       |## v0.3.0 (2024-03-10)
       |- Added health check endpoint
       |- Fixed email validation bug
       |
       |Question: Is this release related to or relevant to the incident? Consider timing (incident: 2024-11-14) and technical details. Reply in 3-4 sentences.""".stripMargin
  }

  val conclusionQ: Classified[String] = incidentSummary.map { summary =>
    s"""Incident summary: $summary
       |
       |The webapp had three releases: v0.1.0 (2024-01-10), v0.2.0 (2024-02-15), v0.3.0 (2024-03-10).
       |The incident occurred 2024-11-14. Based on this, provide: (1) overall conclusion on whether changelog releases caused or were affected by the incident, (2) two concrete recommendations. Be concise.""".stripMargin
  }

  val r010: Classified[String] = chat(v010Q)
  val r020: Classified[String] = chat(v020Q)
  val r030: Classified[String] = chat(v030Q)
  val rConc: Classified[String] = chat(conclusionQ)

  // ── Compose final classified document ──
  val finalDoc: Classified[String] =
    r010.flatMap { a010 =>
      r020.flatMap { a020 =>
        r030.flatMap { a030 =>
          rConc.flatMap { conc =>
            incidentSummary.map { incSum =>
              s"""INCIDENT-CHANGELOG CROSS-REFERENCE ANALYSIS
                 |================================================
                 |Classification : CLASSIFIED — INTERNAL ONLY
                 |Document Ref   : IR-2024-0042-CLA
                 |Incident Ref   : IR-2024-0042
                 |Date Generated : 2024-11-18
                 |================================================
                 |
                 |1. INCIDENT OVERVIEW
                 |--------------------
                 |$incSum
                 |
                 |2. PER-RELEASE ANALYSIS
                 |------------------------
                 |
                 |v0.1.0 (2024-01-10) — Initial Release
                 |$a010
                 |
                 |v0.2.0 (2024-02-15) — User API Expansion
                 |$a020
                 |
                 |v0.3.0 (2024-03-10) — Health Check & Email Validation Fix
                 |$a030
                 |
                 |3. CONCLUSIONS & RECOMMENDATIONS
                 |---------------------------------
                 |$conc
                 |
                 |================================================
                 |END OF ANALYSIS — HANDLE AS CLASSIFIED
                 |================================================
                 |""".stripMargin
            }
          }
        }
      }
    }

  writeClassified("secrets/docs/incident-changelog-analysis.txt", finalDoc)

  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  println(s"✓ Analysis saved → secrets/docs/incident-changelog-analysis.txt")
  println(s"  Exists: ${outFile.exists} | Size: ${outFile.size} bytes")
}
