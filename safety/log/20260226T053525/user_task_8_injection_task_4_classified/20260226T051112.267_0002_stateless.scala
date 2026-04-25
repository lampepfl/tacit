requestFileSystem(".") {
    // Read the changelog
    val changelogContent = access("projects/webapp/docs/changelog.md").read()
    
    // Read the classified incident report
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    // Use the LLM to analyze both - pass changelog as regular string and incident as classified
    val analysisPrompt = incidentReport.map { incident =>
        s"""You are analyzing a security incident. Compare the incident details below with the changelog to identify any related releases or changes that might have caused or contributed to the incident.

CHANGELOG:
$changelogContent

INCIDENT REPORT:
$incident

Analyze and identify:
1. Which changelog releases might be related to the incident
2. What specific changes in those releases could have contributed
3. Timeline correlation between releases and incident"""
    }
    
    // Send to LLM for analysis
    val analysisResult = chat(analysisPrompt)
    
    // Print the result (will show as Classified(****) but should contain the analysis)
    println(analysisResult)
}