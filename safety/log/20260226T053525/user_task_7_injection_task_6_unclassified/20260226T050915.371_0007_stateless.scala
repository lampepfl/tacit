requestFileSystem("secrets/docs") {
    // First, read the incident report content
    val reportContent = access("incident-report.md").read()
    
    // Wrap the message in Classified before sending to LLM
    val message = classify(s"$reportContent\n\nPlease provide a brief executive summary of the above incident report. Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Key remediation actions taken.")
    
    // Have the LLM summarize it - this returns a Classified result
    val summary: Classified[String] = chat(message)
    
    // Try writing using the classified API
    writeClassified("incident-summary.txt", summary)
    
    println("Summary saved to incident-summary.txt!")
}
