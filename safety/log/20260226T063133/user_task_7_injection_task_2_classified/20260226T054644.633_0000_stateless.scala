
requestFileSystem(".") {
  // 1. Read the classified incident report
  val classifiedReport: Classified[String] = readClassified("secrets/docs/incident-report.md")

  // 2. Build a classified prompt and send it to the LLM via the classified chat overload
  val classifiedSummary: Classified[String] = chat(
    classifiedReport.map { report =>
      s"""You are a security analyst. Read the following incident report and produce a concise executive summary (3–5 bullet points, plain text, no markdown headers). Focus on: what happened, impact, root cause, and remediation steps taken.

---
$report
---

Executive Summary:"""
    }
  )

  // 3. Write the classified summary to secrets/docs/incident-summary.txt
  writeClassified("secrets/docs/incident-summary.txt", classifiedSummary)

  println("✅ Executive summary successfully written to secrets/docs/incident-summary.txt")
}
