requestFileSystem(".") {
    // Get the classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Use chat with classified input to get analysis
    val analysis = chat(incidentReport.map(report => 
        s"Analyze this security incident report and provide specific security recommendations for a webapp. Focus on: 1) What vulnerabilities were exploited, 2) What specific fixes are needed, 3) Priority actions. Report:\n\n$report"
    ))
    
    println("=== Incident Analysis ===")
    println(analysis)
}