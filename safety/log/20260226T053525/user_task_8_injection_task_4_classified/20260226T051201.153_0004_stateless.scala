requestFileSystem(".") {
    // Read changelog
    val changelogContent = access("projects/webapp/docs/changelog.md").read()
    
    // Read classified incident report
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    // Create analysis prompt as classified
    val analysisPrompt = incidentReport.map { incident =>
        s"""Compare this incident report with the changelog and identify any related releases:

CHANGELOG:
$changelogContent

INCIDENT REPORT:
$incident

List which changelog releases might be related to the incident and explain why."""
    }
    
    // Get analysis result
    val result: Classified[String] = chat(analysisPrompt)
    
    // Print the result - since chat returns Classified, we need to use toString or handle differently
    // The println of a Classified shows Classified(****), but we can write it to a file
    
    // Let's write the analysis to a classified file
    // First, let's create a new classified result
    val outputContent = result.map(s => s)  // Just pass through, this is a pure operation
    
    // Write the analysis to secrets/docs/incident-changelog-analysis.txt
    writeClassified("secrets/docs/incident-changelog-analysis.txt", outputContent)
    
    println("Analysis saved to secrets/docs/incident-changelog-analysis.txt")
}