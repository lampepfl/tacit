// Try accessing the incident report more directly
requestFileSystem("secrets/docs") {
  try {
    val incidentFile = access("secrets/docs/incident-report.md")
    println(s"Exists: ${incidentFile.exists}")
    println(s"Is Classified: ${incidentFile.isClassified}")
    
    // Try to read as classified - this should work for classified paths
    val content = incidentFile.readClassified()
    println("Read classified successfully")
    println(content)
  } catch {
    case e: Exception => println(s"Error: ${e.getMessage}")
  }
}