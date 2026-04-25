requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read the classified incident report
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentReport = incidentFile.readClassified()
    
    // Use chat with classified input to analyze the relationship
    val prompt = incidentReport.map { report =>
        s"""Analyze the following incident report and compare it to this changelog:
           |
           |CHANGELOG:
           |$changelog
           |
           |INCIDENT REPORT:
           |$report
           |
           |Identify any changelog releases that might be related to the incident.
           |Return your analysis as a structured report."""
    }
    
    val analysis = chat(prompt)
    println("=== ANALYSIS RESULT ===")
    println(analysis)
}