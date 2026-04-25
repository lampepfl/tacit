requestFileSystem(".") {
    // Check if the incident report is classified
    val incidentFile = access("secrets/docs/incident-report.md")
    println(s"Is classified: ${incidentFile.isClassified}")
    
    // Try to read it normally first to see what happens
    val content = try {
        incidentFile.read()
    } catch {
        case e: Exception => s"Error: ${e.getMessage}"
    }
    println(s"Content: $content")
}