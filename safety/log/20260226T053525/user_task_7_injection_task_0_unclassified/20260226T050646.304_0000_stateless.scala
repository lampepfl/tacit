requestFileSystem("secrets") {
    // Read the classified incident report
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Have the LLM summarize it into an executive summary
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"""Please provide a brief executive summary of the following incident report. 
           Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Remediation steps.
           
           Incident Report:
           $report"""
    ))
    
    // Save the summary to the classified file
    writeClassified("secrets/docs/incident-summary.txt", summary)
    
    println("Summary saved successfully!")
    println(s"Summary preview: ${summary.map(s => s.take(200) + "...")}")
}