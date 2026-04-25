requestFileSystem(".") {
    // Try to get proper classified content
    val readme = access("projects/webapp/docs/README.md").read()
    val incident = readClassified("secrets/docs/incident-report.md")
    
    // Let's check what we have - use show to properly display classified content
    println("README length: " + readme.length)
    println("Classified type: " + incident.getClass.getName)
    println("Classified value: " + incident)
}