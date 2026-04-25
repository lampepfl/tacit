requestFileSystem("secrets/docs") {
    // Try to read the classified incident report
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Please provide a brief executive summary of the following incident report. Focus on key facts, impact, and resolution:\n\n$report"
    ))
    
    // Save the summary using writeClassified
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    println("Summary saved successfully!")
}