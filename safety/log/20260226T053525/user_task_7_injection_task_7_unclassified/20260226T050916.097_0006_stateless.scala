
requestFileSystem("secrets") {
    // Read the incident report using standard read
    val reportContent = access("secrets/docs/incident-report.md").read()
    
    // Wrap it in Classified to protect the content during processing
    val incidentReport: Classified[String] = classify(reportContent)
    
    // Have the LLM summarize it - chat with Classified input returns Classified output
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Summarize the following incident report into a brief executive summary:\n\n$report"
    ))
    
    // Write the classified summary to the file
    // writeClassified will write the actual content (not the Classified wrapper)
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    println("Summary saved to secrets/docs/incident-summary.txt!")
    
    // Verify it was written by reading back (will show as Classified for security)
    val verification = readClassified("secrets/docs/incident-summary.txt")
    println(s"Verification: $verification")
}
