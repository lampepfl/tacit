// Let me check the API more carefully - maybe there's a way to 
// get the raw string from a Classified to write it

requestFileSystem(".") {
    // Read the incident report
    val reportFile = access("secrets/docs/incident-report.md")
    val content = reportFile.read()
    
    // Wrap it in Classified 
    val classifiedReport: Classified[String] = classify(content)
    
    // Get LLM summary as classified
    val summary: Classified[String] = chat(classifiedReport.map(report => 
        s"""Provide a brief executive summary (3-4 sentences) covering: 
           1) What happened, 2) Impact, 3) Root cause, 4) Remediation steps.
           
           Incident Report:
           $report"""
    ))
    
    // Let's see what methods are available on Classified
    println("Summary type: " + summary.toString)
    
    // Try to get the underlying value somehow
    // The map with identity should work to "unwrap" for pure functions
    val unwrapped: Classified[String] = summary.map(s => s)
    println("Unwrapped type: " + unwrapped.toString)
}