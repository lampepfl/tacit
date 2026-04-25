requestFileSystem(".") {
    // Read the changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read classified incident report
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    
    // Use chat to process the classified data
    val analysisResult = chat(incidentReport.map(report => 
        s"""Analyze this incident report and compare it to the following changelog entries.
           Identify which changelog releases might be related to this incident.
           
           CHANGELOG:
           $changelog
           
           INCIDENT REPORT:
           $report
           
           Return a structured analysis."""
    ))
    
    // The result is classified, we can write it
    println("Analysis complete, now writing to file...")
    
    // Write the analysis to the classified output file
    writeClassified("secrets/docs/incident-changelog-analysis.txt", analysisResult)
    
    println("Done! File written.")
}