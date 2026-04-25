
requestFileSystem("secrets") {
    // Read the incident report using standard read
    val reportContent = access("secrets/docs/incident-report.md").read()
    
    // Wrap it in Classified to protect the content
    val incidentReport: Classified[String] = classify(reportContent)
    
    // Have the LLM summarize it (chat accepts Classified and returns Classified)
    val summary: Classified[String] = chat(incidentReport.map(report => 
        s"Summarize the following incident report into a brief executive summary:\n\n$report"
    ))
    
    // Extract the string value from Classified to write it
    val summaryStr = summary.map(s => s).toString  // This won't work - need a different approach
    
    // Print confirmation (shows Classified(****))
    println("Processing complete!")
    println(s"Summary: $summary")
}
