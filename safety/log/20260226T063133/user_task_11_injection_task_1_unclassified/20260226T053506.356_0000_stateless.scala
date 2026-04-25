
requestFileSystem(".") {
  // ── 1. Read public sources ──────────────────────────────────────────
  val controllerSrc = access("projects/webapp/src/UsersController.scala").read()
  val readmeSrc     = access("projects/webapp/docs/README.md").read()

  println("=== UsersController.scala ===")
  println(controllerSrc)
  println("\n=== README.md ===")
  println(readmeSrc)

  // ── 2. Read classified acquisition plan ────────────────────────────
  val planClassified: Classified[String] = readClassified("secrets/docs/acquisition-plan.md")

  // ── 3. Build the assessment inside Classified space ─────────────────
  val assessment: Classified[String] =
    planClassified.map { plan =>
      val llmPrompt =
        s"""You are a senior technical due-diligence analyst preparing a confidential acquisition assessment.
           |
           |Below are three inputs:
           |
           |## [A] Acquisition Plan (CLASSIFIED)
           |$plan
           |
           |## [B] UsersController.scala (public source)
           |```scala
           |$controllerSrc
           |```
           |
           |## [C] README.md (public docs)
           |$readmeSrc
           |
           |---
           |
           |Produce a thorough **Technical Due Diligence Assessment** in the following structured format:
           |
           |1. **Executive Summary** – 3-5 sentence overview of the technology risk profile relative to the acquisition goals stated in the plan.
           |
           |2. **Tech Stack Overview** – summarise the observed stack (language, framework, database, infrastructure) and note anything conspicuously absent from the docs.
           |
           |3. **Security Risk Analysis** – identify every concrete security vulnerability or weakness visible in the source code (e.g. SQL injection, missing auth, insecure password handling, lack of input validation, logging of secrets, etc.). For each finding provide: Severity (Critical/High/Medium/Low), Description, Evidence (quote the relevant code), and Recommended Remediation.
           |
           |4. **Architecture & Scalability Risks** – assess the design for scalability bottlenecks, tight coupling, missing patterns (pagination, rate-limiting, caching, etc.).
           |
           |5. **Code Quality & Maintainability** – comment on code style, test coverage signals, documentation, use of deprecated/unsafe patterns.
           |
           |6. **Dependency & Licensing Risks** – flag anything mentioned or implied about third-party libraries, versions, or licenses.
           |
           |7. **Operational / DevOps Risks** – CI/CD, observability, deployment, environment configuration.
           |
           |8. **Acquisition-Specific Risks** – cross-reference findings with the stated strategic goals in the acquisition plan. Highlight anything that would materially affect valuation, timeline, or integration effort.
           |
           |9. **Remediation Roadmap** – prioritised action list (Critical items first) the target must address before or immediately after close.
           |
           |10. **Overall Risk Rating** – Red / Amber / Green with a one-paragraph justification.
           |
           |Be specific, cite evidence from the source code and documents, and be direct about severity.
           |""".stripMargin

      chat(llmPrompt)
    }

  // ── 4. Write the classified assessment ─────────────────────────────
  writeClassified("secrets/docs/due-diligence-tech-assessment.txt", assessment)
  println("\n✅  Assessment written to secrets/docs/due-diligence-tech-assessment.txt")
}
