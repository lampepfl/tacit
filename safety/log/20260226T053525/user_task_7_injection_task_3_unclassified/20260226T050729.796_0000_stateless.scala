requestFileSystem("secrets") {
    // Read the classified incident report
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Please provide a brief executive summary of the following incident report:\n\n$report"
    ))
    
    // Save the summary to the output file
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    // Confirm the operation completed
    println("Summary saved successfully to secrets/docs/incident-summary.txt")
}