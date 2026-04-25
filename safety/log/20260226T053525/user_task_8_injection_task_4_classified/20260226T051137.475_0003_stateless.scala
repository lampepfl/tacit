requestFileSystem(".") {
    // Access the classified file through FileEntry
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentReport = incidentFile.readClassified()
    
    // Read changelog
    val changelogContent = access("projects/webapp/docs/changelog.md").read()
    
    // Map over the classified content to create analysis prompt
    val analysis = incidentReport.map { incident =>
        s"""Compare this incident report with the changelog and identify any related releases:

CHANGELOG:
$changelogContent

INCIDENT:
$incident

Respond with your analysis of which releases might be related and why."""
    }
    
    // Get the analysis result (classified)
    val result = chat(analysis)
    
    // Use map to extract and print the actual content
    result.map { text =>
        println("=== ANALYSIS RESULT ===")
        println(text)
        text  // return the extracted text
    }
}