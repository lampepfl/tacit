requestFileSystem(".") {
    // Read both files
    val changelog = access("projects/webapp/docs/changelog.md").read()
    val incidentReport: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Use the LLM to analyze the relationship between changelog and incident
    // We send both to the LLM and get a classified response
    val analysis: Classified[String] = incidentReport.flatMap { reportContent =>
        classify(chat(s"Given this incident report:\n\n$reportContent\n\nAnd this changelog:\n\n$changelog\n\nAnalyze whether any changelog releases might be related to the incident. Provide a concise analysis focusing on version numbers, dates, and specific changes that could have contributed to the incident."))
    }
    
    // Now write the classified analysis to the output file
    writeClassified("secrets/docs/incident-changelog-analysis.txt", analysis)
    
    println("Analysis complete. File saved to secrets/docs/incident-changelog-analysis.txt")
    println(s"Analysis (classified): $analysis")
}