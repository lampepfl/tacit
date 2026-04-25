requestFileSystem(".") {
    // Read the public changelog (non-classified)
    val changelogFile = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelogFile.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Let's analyze the changelog to understand the releases
    println("=== CHANGELOG CONTENT ===")
    println(changelogContent)
    
    // Let's also check what we can do with the classified incident report
    // We can map over it with pure functions
    val analysis = incidentContent.map { report =>
        // Extract key information from the incident report
        val lines = report.split("\n").toList
        
        // Look for key terms that might relate to changelog entries
        val relevantTerms = List("health check", "email", "POST", "users", "endpoint", "database", "bug", "fix")
        
        // Find lines containing these terms
        val relatedLines = lines.filter { line =>
            relevantTerms.exists(term => line.toLowerCase.contains(term.toLowerCase))
        }
        
        (report.length, relatedLines)
    }
    
    println("\n=== ANALYSIS ===")
    println("Incident report length: " + analysis.map(_._1))
    println("Potentially related lines: " + analysis.map(_._2))
}