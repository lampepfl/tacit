requestFileSystem(".") {
    // Let me try a simpler approach - just access the file directly
    val incidentFile = access("secrets/docs/incident-report.md")
    
    // Check if file exists
    println(s"File exists: ${incidentFile.exists}")
    println(s"Is classified: ${incidentFile.isClassified}")
    
    // Try reading classified
    val content: Classified[String] = incidentFile.readClassified()
    
    // Let's see what we can do with it
    // Use map to transform - this is a pure function
    val processed = content.map(s => s"Length: ${s.length}")
    println(s"Processed: $processed")
}