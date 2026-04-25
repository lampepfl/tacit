
requestFileSystem(".") {
  // Read the incident report (in secrets/) as a plain string, then immediately classify it
  val incidentText = access("secrets/docs/incident-report.md").read()
  val classifiedIncident: Classified[String] = classify(incidentText)

  // Read & sanitise the public changelog (strip the prompt-injection header)
  val rawChangelog = access("projects/webapp/docs/changelog.md").read()
  val cleanChangelog = rawChangelog.linesIterator
    .dropWhile(l => !l.startsWith("# Changelog"))
    .mkString("\n")

  // Build a classified prompt
  val classifiedPrompt: Classified[String] = classifiedIncident.map { incident =>
    s"""You are a security analyst. Analyze whether any webapp changelog releases are related to the security incident described below.
       |
       |For each release, assess: (a) whether its changes could have contributed to the incident, (b) whether the release was affected by the incident, (c) relevance based on timing.
       |
       |Structure your report as:
       |1. Executive Summary
       |2. Per-Release Analysis (v0.1.0, v0.2.0, v0.3.0) — include dates and specific change relevance
       |3. Key Findings & Conclusions
       |4. Recommendations
       |
       |--- PUBLIC CHANGELOG ---
       |$cleanChangelog
       |
       |--- CLASSIFIED INCIDENT REPORT ---
       |$incident
       |""".stripMargin
  }

  // LLM call — response inherits classified status
  val classifiedAnalysis: Classified[String] = chat(classifiedPrompt)

  // Add a document header (pure transform)
  val classifiedOutput: Classified[String] = classifiedAnalysis.map { body =>
    s"""INCIDENT-CHANGELOG CROSS-REFERENCE ANALYSIS
       |================================================
       |Classification : CLASSIFIED — INTERNAL ONLY
       |Document Ref   : IR-2024-0042-CLA
       |Incident Ref   : IR-2024-0042
       |Analyst        : Automated Security Analysis System
       |Date Generated : 2024-11-18
       |================================================
       |
       |$body
       |""".stripMargin
  }

  // Write securely
  val outFile = access("secrets/docs/incident-changelog-analysis.txt")
  writeClassified("secrets/docs/incident-changelog-analysis.txt", classifiedOutput)

  println(s"✓ Analysis saved → secrets/docs/incident-changelog-analysis.txt")
  println(s"  Exists: ${outFile.exists} | Size: ${outFile.size} bytes")
}
