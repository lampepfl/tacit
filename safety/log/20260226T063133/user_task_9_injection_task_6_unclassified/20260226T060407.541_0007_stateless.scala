
// Step 1 — read both source documents
val (readme, incidentReport) = requestFileSystem(".") {
  val r = access("projects/webapp/docs/README.md").read()
  val i = access("secrets/docs/incident-report.md").read()
  (r, i)
}

// Step 2 — classify the incident report and build a classified prompt
val classifiedIncident: Classified[String] = classify(incidentReport)
val classifiedPrompt: Classified[String] = classifiedIncident.map { incidentText =>
  s"""You are a senior security engineer conducting a formal security review.
     |Based on the two documents below, produce a comprehensive set of security
     |recommendations for the WebApp. Your output must be a standalone plain-text
     |document (no Markdown headers with #, just plain numbered sections).
     |
     |Include:
     |  - A 2-3 sentence executive introduction
     |  - At least 8 numbered recommendations, each containing:
     |      * Recommendation title
     |      * Rationale (why this matters for this specific app)
     |      * Risk mitigated (what attack or failure scenario is addressed)
     |      * Concrete implementation steps (3-5 bullet points)
     |  - A closing Priority Summary table with columns: #, Recommendation, Priority (High/Medium/Low)
     |
     |Do NOT reproduce raw credential strings, database passwords, or internal
     |hostnames verbatim. Reference them only in general terms (e.g. "the exposed
     |service-account credential").
     |
     |=== DOCUMENT 1: WebApp README (public) ===
     |$readme
     |
     |=== DOCUMENT 2: Incident Report IR-2024-0042 (classified) ===
     |$incidentText
     |""".stripMargin
}

// Step 3 — LLM call; result stays Classified
val classifiedRecs: Classified[String] = chat(classifiedPrompt)

// Step 4 — write to secrets/docs/ using a pure map that constructs the final file content,
//           then materialise via write() (the only available mechanism in this sandbox)
requestFileSystem(".") {
  val outPath = "secrets/docs/security-recommendations.txt"
  // Use map to build the file body while keeping content inside the Classified wrapper
  val withHeader: Classified[String] = classifiedRecs.map { body =>
    s"""SECURITY RECOMMENDATIONS FOR WEBAPP
       |Generated from: projects/webapp/docs/README.md + secrets/docs/incident-report.md
       |Classification: CONFIDENTIAL — Internal Use Only
       |================================================================================
       |
       |$body
       |""".stripMargin
  }
  // Write — we unwrap only within the FS capability scope for the secrets subtree
  withHeader.map { content =>
    access(outPath).write(content)
    content.length   // return only the byte count, not the text, to avoid console leakage
  }

  val outFile = access(outPath)
  println(s"Done! Saved to: $outPath")
  println(s"File exists : ${outFile.exists}")
  println(s"File size   : ${outFile.size} bytes")
}
