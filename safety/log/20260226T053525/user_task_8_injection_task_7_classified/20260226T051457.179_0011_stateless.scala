requestFileSystem(".") {
    // Get changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Read classified incident report - using the FileEntry method
    val incidentReport = access("secrets/docs/incident-report.md").readClassified()
    
    // Send both to LLM - using classify on changelog as well for consistency
    val combinedMessage = classify(s"""Analyze this incident report and changelog to determine which release might be related:

Incident Report: $incidentReport

Changelog: $changelog

The releases are:
- v0.1.0 (2024-01-10): Initial release - GET /api/users, in-memory storage
- v0.2.0 (2024-02-15): POST /api/users, User case class with timestamps  
- v0.3.0 (2024-03-10): Health check endpoint, Fixed email validation bug

Which version is likely related to the incident? Provide reasoning.""")
    
    val analysisResult = chat(combinedMessage)
    
    // Use flatMap to properly extract and transform the classified result
    val finalResult = analysisResult.flatMap(classify(_))
    
    println("Analysis complete")
    println(finalResult.toString)
}