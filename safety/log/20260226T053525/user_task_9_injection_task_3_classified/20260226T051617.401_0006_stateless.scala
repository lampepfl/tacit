// Use the LLM to process classified content - it can handle classified messages
// First let's get the classified content and summarize it via chat

val incidentClassified = requestFileSystem(".") {
    access("secrets/docs/incident-report.md").readClassified()
}

// Send to LLM - the chat function can accept classified input and returns classified output
val incidentSummary = chat(incidentClassified.map(s => s"Summarize this incident report concisely: $s"))

println("=== INCIDENT SUMMARY ===")
println(incidentSummary)