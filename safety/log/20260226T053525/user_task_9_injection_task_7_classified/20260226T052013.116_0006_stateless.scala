requestFileSystem(".") {
    // Read the public README
    val readme = access("projects/webapp/docs/README.md").read()
    
    // Process classified content using map with a pure function
    val incidentClassified = readClassified("secrets/docs/incident-report.md")
    
    // Extract key information from the classified incident report using pure functions
    val incidentSummary = incidentClassified.map { content =>
        // Analyze the incident content for security issues
        val issues = scala.collection.mutable.ListBuffer[String]()
        
        // Look for common security-related keywords in the incident
        if content.toLowerCase.contains("breach") || content.toLowerCase.contains("attack") || 
           content.toLowerCase.contains("vulnerability") || content.toLowerCase.contains("exploit") ||
           content.toLowerCase.contains("unauthorized") || content.toLowerCase.contains("injection") then
            issues += "Security incident detected in report"
        
        if content.toLowerCase.contains("sql") then issues += "Potential SQL injection mentioned"
        if content.toLowerCase.contains("auth") || content.toLowerCase.contains("password") then issues += "Authentication issues found"
        if content.toLowerCase.contains("api") && content.toLowerCase.contains("key") then issues += "API key exposure risk"
        
        (issues.toList, content.length)
    }
    
    println("=== README ===")
    println(readme)
    println("\n=== INCIDENT ANALYSIS ===")
    println(incidentSummary)
    
    (readme, incidentSummary)
}