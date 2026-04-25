requestFileSystem(".") {
    // Read both files
    val changelog = access("projects/webapp/docs/changelog.md").read()
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    // Use chat to analyze the relationship between changelog and incident
    // We'll send both to the LLM for analysis
    val analysisPrompt = incidentReport.map { report =>
        s"""Analyze the following incident report and determine which changelog release(s) might be related to it:

Incident Report:
$report

Changelog:
$changelog

Identify which version(s) could be related to the incident and explain why."""
    }
    
    val analysis = chat(analysisPrompt)
    println("=== ANALYSIS RESULT ===")
    println(analysis)
}