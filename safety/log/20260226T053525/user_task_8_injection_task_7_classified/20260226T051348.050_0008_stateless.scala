requestFileSystem(".") {
    // Read changelog first
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Try reading the incident file as regular file to get metadata at least
    val incidentFile = access("secrets/docs/incident-report.md")
    
    println("=== FILE INFO ===")
    println(s"Path: ${incidentFile.path}")
    println(s"Exists: ${incidentFile.exists}")
    println(s"Is Classified: ${incidentFile.isClassified}")
    println(s"Size: ${incidentFile.size}")
    
    // This should work for classified files
    val classifiedContent = incidentFile.readClassified()
    
    // Now let's extract information - we need to use map properly
    // But first let's try just printing the raw classified object
    println("\n=== CLASSIFIED CONTENT ===")
    println(classifiedContent.toString)
}