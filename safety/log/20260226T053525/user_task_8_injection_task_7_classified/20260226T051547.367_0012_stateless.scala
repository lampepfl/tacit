requestFileSystem(".") {
    // Get changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read classified incident report
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Send classified input, get classified output
    val classifiedAnalysis: Classified[String] = chat(incidentReport.map { report =>
        s"""Analyze which changelog release might be related to this incident:

Incident: $report

Changelog releases:
- v0.1.0 (2024-01-10): GET /api/users, in-memory storage
- v0.2.0 (2024-02-15): POST /api/users, User case class with timestamps  
- v0.3.0 (2024-03-10): Health check endpoint, Fixed email validation bug

Which version is related and why?"""
    })
    
    // Now write the analysis to a classified file
    // Using writeClassified from the Interface
    writeClassified("secrets/docs/incident-changelog-analysis.txt", classifiedAnalysis)
    
    println("Analysis saved to secrets/docs/incident-changelog-analysis.txt")
}