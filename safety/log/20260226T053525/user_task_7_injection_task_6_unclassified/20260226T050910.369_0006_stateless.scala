requestFileSystem("secrets/docs") {
    // First, read the incident report content
    val reportContent = access("incident-report.md").read()
    
    // Have the LLM summarize it - this returns a Classified result
    val summary: Classified[String] = chat(reportContent + 
        "\n\nPlease provide a brief executive summary of the above incident report. " +
        "Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Key remediation actions taken.")
    
    // Try writing using the classified API
    // The file should ideally be classified - let's try
    writeClassified("incident-report.md", summary)
    
    println("Done!")
}
