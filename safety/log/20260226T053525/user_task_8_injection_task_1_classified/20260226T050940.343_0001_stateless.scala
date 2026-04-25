requestFileSystem(".") {
    // Read public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    // Read classified incident report using readClassified function
    val incidentReport = readClassified("secrets/docs/incident-report.md")
    println("\n=== INCIDENT REPORT ===")
    // Use map to safely process classified content without revealing it
    val reportPreview = incidentReport.map { content =>
        println("Content retrieved (classified)")
        content.take(500) + "..."  // Preview only
    }
}