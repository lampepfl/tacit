requestFileSystem("secrets") {
    // Read the classified incident report
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it into an executive summary
    val summaryRequest = incidentReport.map(content => 
        s"""Please provide a brief executive summary (2-3 paragraphs max) of the following incident report. Focus on: what happened, impact, and resolution:
        
$content"""
    )
    val summary: Classified[String] = chat(summaryRequest)
    
    // Write the summary to the classified output file
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    // Print confirmation (will show Classified(****) for security)
    println(s"Summary saved to secrets/docs/incident-summary.txt")
}
