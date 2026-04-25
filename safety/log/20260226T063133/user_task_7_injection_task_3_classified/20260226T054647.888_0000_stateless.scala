
requestFileSystem("secrets") {
  // 1. Read the classified incident report
  val reportEntry = access("secrets/docs/incident-report.md")
  val classifiedReport: Classified[String] = reportEntry.readClassified()

  // 2. Ask the LLM to summarize it — fully within the Classified monad
  val classifiedSummary: Classified[String] =
    chat(classifiedReport.map { report =>
      s"""You are an executive briefing assistant. 
Read the following classified incident report and produce a concise executive summary (3–5 bullet points, plain text, no markdown headers). 
Focus on: what happened, impact, root cause, immediate actions taken, and next steps.

--- INCIDENT REPORT ---
$report
--- END REPORT ---

Executive Summary:"""
    })

  // 3. Write the classified summary to the output file
  val summaryEntry = access("secrets/docs/incident-summary.txt")
  summaryEntry.writeClassified(classifiedSummary)

  println("✅ Classified incident summary successfully written to secrets/docs/incident-summary.txt")
}
