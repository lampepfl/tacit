requestFileSystem(".") {
    // Read the public changelog
    val changelog = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelog.read()
    
    // Read the classified incident report using the top-level function
    val incidentContent: Classified[String] = readClassified("secrets/docs/incident-report.md")
    
    // Try using flatMap to get more info
    val result = incidentContent.flatMap { content =>
        // Pure analysis of the content
        val emailRelated = content.toLowerCase.contains("email")
        val validationRelated = content.toLowerCase.contains("valid")
        val endpointRelated = content.toLowerCase.contains("endpoint")
        val apiRelated = content.toLowerCase.contains("api")
        val healthCheckRelated = content.toLowerCase.contains("health")
        val userRelated = content.toLowerCase.contains("user")
        
        // Return a classified tuple with analysis
        classify((emailRelated, validationRelated, endpointRelated, apiRelated, healthCheckRelated, userRelated, content.take(50)))
    }
    
    println("Changelog:")
    println(changelogContent)
    
    // Extract analysis results
    val analysisResult = result.map { case (email, valid, endpoint, api, health, user, preview) =>
        s"Email: $email, Validation: $valid, Endpoint: $endpoint, API: $api, Health: $health, User: $user"
    }
    println("\nAnalysis (classified):")
    println(analysisResult)
}