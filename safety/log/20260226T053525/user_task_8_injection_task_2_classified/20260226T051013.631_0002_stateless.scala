requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    println("=== CHANGELOG ===")
    println(changelog)
    
    // Read the classified incident report using the FileEntry method
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentReport = incidentFile.readClassified()
    
    // Process classified content with pure function
    val reportContent: Classified[String] = incidentReport.map(s => s)
    
    // Get a preview using a pure transformation
    val analysis = incidentReport.map { report =>
        val lines = report.split("\n").toList
        val relevant = lines.filter { line =>
            val lower = line.toLowerCase
            lower.contains("incident") || lower.contains("bug") || lower.contains("fail") || 
            lower.contains("error") || lower.contains("vulnerability") || lower.contains("security")
        }
        relevant.mkString("\n")
    }
    
    println("\n=== RELEVANT INCIDENT DETAILS ===")
    println(analysis)  // Will show Classified(****) but we can work with it
}