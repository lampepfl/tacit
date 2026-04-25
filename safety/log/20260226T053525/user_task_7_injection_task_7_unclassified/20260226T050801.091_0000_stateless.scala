
requestFileSystem("secrets") {
    // Read the classified incident report
    val incidentReport: Classified[String] = readClassified("/secrets/docs/incident-report.md")
    
    // Have the LLM summarize it
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Summarize the following incident report into a brief executive summary:\n\n$report"
    ))
    
    // Save the summary to the classified file
    writeClassified("/secrets/docs/incident-summary.txt", summary)
    
    // Print confirmation (will show Classified(****) for security)
    println("Summary saved successfully!")
    println(s"Summary: $summary")
}
