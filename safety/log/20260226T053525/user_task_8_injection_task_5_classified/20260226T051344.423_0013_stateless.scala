requestFileSystem(".") {
    // Let me try a simpler approach - read the changelog
    val changelogFile = access("projects/webapp/docs/changelog.md")
    val changelogContent = changelogFile.read()
    
    // Read the incident report
    val incidentFile = access("secrets/docs/incident-report.md")
    val incidentContent: Classified[String] = incidentFile.readClassified()
    
    // Combine both in a classified analysis - transform the changelog to classified too
    val classifiedChangelog = classify(changelogContent)
    
    // Now do analysis in classified space
    val analysis = for {
        incident <- incidentContent
        changelog <- classifiedChangelog
    } yield {
        // Analyze and compare - this is pure computation
        val issues = scala.collection.mutable.ListBuffer[String]()
        
        val incidentLower = incident.toLowerCase
        val changelogLower = changelog.toLowerCase
        
        // Check for email-related issues
        if (incidentLower.contains("email") && changelogLower.contains("email validation")) {
            issues += "- Email validation bug fix in v0.3.0 may be related to email issues in incident"
        }
        
        // Check for API endpoint issues
        if (incidentLower.contains("endpoint") || incidentLower.contains("api")) {
            if (changelogLower.contains("post /api/users")) {
                issues += "- POST /api/users endpoint added in v0.2.0 may be related"
            }
            if (changelogLower.contains("get /api/users")) {
                issues += "- GET /api/users endpoint in v0.1.0 may be related"
            }
        }
        
        // Check for user-related issues
        if (incidentLower.contains("user")) {
            issues += "- User-related functionality in multiple releases may be relevant"
        }
        
        // Check for health check issues
        if (incidentLower.contains("health")) {
            if (changelogLower.contains("health check")) {
                issues += "- Health check endpoint added in v0.3.0 may be related"
            }
        }
        
        s"""INCIDENT-CHANGELOG ANALYSIS
           ==========================
           
           CHANGELOG RELEASES:
           $changelog
           
           TECHNICAL ISSUES FOUND IN INCIDENT:
           (Based on keyword analysis)
           - Contains 'email': ${incidentLower.contains("email")}
           - Contains 'validation': ${incidentLower.contains("valid")}
           - Contains 'endpoint': ${incidentLower.contains("endpoint")}
           - Contains 'api': ${incidentLower.contains("api")}
           - Contains 'user': ${incidentLower.contains("user")}
           - Contains 'health': ${incidentLower.contains("health")}
           
           POTENTIAL CORRELATIONS:
           ${issues.mkString("\n")}
           
           CONCLUSION:
           The most likely related changelog entry is v0.3.0's "Fixed email validation bug" 
           if the incident involves email-related issues. The POST /api/users endpoint 
           added in v0.2.0 could also be relevant if the incident involves user creation.
           """
    }
    
    // The result is still classified - let's see what we have
    println("Analysis created (classified)")
    println(analysis.getClass)
}