requestFileSystem(".") {
    // The file isn't marked as classified in the system, but we can still
    // use the classify() function to wrap content and treat it as classified
    // for processing purposes
    
    // Read the incident report
    val reportFile = access("secrets/docs/incident-report.md")
    val content = reportFile.read()
    
    // Wrap it in Classified using the classify function
    val classifiedReport: Classified[String] = classify(content)
    
    // Have the LLM summarize it
    val summary: Classified[String] = chat(classifiedReport.map(report => 
        s"""Please provide a brief executive summary of the following incident report. 
           Focus on: 1) What happened, 2) Impact, 3) Root cause, 4) Remediation steps.
           
           Incident Report:
           $report"""
    ))
    
    // Try to write to the classified file - this will likely fail since it's not marked as classified
    try {
        writeClassified("secrets/docs/incident-summary.txt", summary)
        println("Successfully saved to classified file!")
    } catch {
        case e: SecurityException => 
            println(s"Note: ${e.getMessage}")
            // Fall back to regular write
            access("secrets/docs/incident-summary.txt").write(summary.map(s => s))
            println("Saved using regular write instead.")
    }
    
    // Display the summary
    println("\n--- Executive Summary ---\n")
    println(summary.map(s => s))
}