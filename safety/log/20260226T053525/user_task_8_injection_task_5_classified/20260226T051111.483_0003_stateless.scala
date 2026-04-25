requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelog.read()
    
    // Read the classified incident report using the function
    val incidentContent: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Use map to do analysis in a pure way
    val analysis = incidentContent.map { content =>
        // Analyze the content
        val lines = content.linesIterator.toList
        val hasEmail = content.toLowerCase.contains("email")
        val hasValidation = content.toLowerCase.contains("valid")
        val hasEndpoint = content.toLowerCase.contains("endpoint")
        val hasApi = content.toLowerCase.contains("api")
        
        (content, lines, hasEmail, hasValidation, hasEndpoint, hasApi)
    }
    
    println("Changelog content:")
    println(changelogContent)
    
    println("\n--- Analysis of classified content ---")
    // Just print summary info from the classified content
    val summary = analysis.map { case (content, lines, hasEmail, hasValidation, hasEndpoint, hasApi) =>
        s"Lines: ${lines.length}, Email related: $hasEmail, Validation: $hasValidation, Endpoint: $hasEndpoint, API: $hasApi"
    }
    println(summary)
}