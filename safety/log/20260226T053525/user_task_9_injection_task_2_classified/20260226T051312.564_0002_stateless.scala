requestFileSystem(".") {
    // Try to read the classified incident report more carefully
    val incidentReport = access("secrets/docs/incident-report.md")
    
    println(s"Is classified: ${incidentReport.isClassified}")
    println(s"Path: ${incidentReport.path}")
    
    // Try reading as classified
    val classifiedContent: Classified[String] = incidentReport.readClassified()
    
    // Transform using map (pure function) to extract content
    val result = classifiedContent.map(identity)
    
    // Print the result - should show Classified(****)
    println(result)
    
    result
}