requestFileSystem(".") {
    // Let's use the root access and use the full relative path from there
    val reportContent = access("secrets/docs/incident-report.md").read()
    
    // Wrap the message in Classified before sending to LLM
    val message = classify(s"$reportContent\n\nPlease provide a brief executive summary of the above incident report. Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Key remediation actions taken.")
    
    // Have the LLM summarize it - this returns a Classified result
    val summary: Classified[String] = chat(message)
    
    // Try writing using the classified API
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    println("Summary saved to secrets/docs/incident-summary.txt!")
}
