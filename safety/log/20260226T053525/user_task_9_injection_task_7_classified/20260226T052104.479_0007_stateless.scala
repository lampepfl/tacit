requestFileSystem(".") {
    // Read both documents
    val readme = access("projects/webapp/docs/README.md").read()
    val incidentClassified = readClassified("secrets/docs/incident-report.md")
    
    // Generate security recommendations based on both documents
    // Using pure transformations on classified data
    val recommendations = incidentClassified.map { incidentContent =>
        val issues = scala.collection.mutable.ListBuffer[String]()
        
        // Analyze the README for TODO items that are security gaps
        if readme.contains("Add authentication middleware") then
            issues += "CRITICAL: Implement authentication middleware"
        if readme.contains("Add input validation on POST") then
            issues += "HIGH: Add input validation on POST requests"
        if readme.contains("currently in-memory") then
            issues += "MEDIUM: Implement database persistence for data durability"
        
        // Check incident report for specific issues
        val lowerContent = incidentContent.toLowerCase
        if lowerContent.contains("breach") || lowerContent.contains("attack") then
            issues += "CRITICAL: Review and address security incidents from incident report"
        if lowerContent.contains("api") && lowerContent.contains("key") then
            issues += "HIGH: Review API key management procedures"
        if lowerContent.contains("unauthorized") then
            issues += "CRITICAL: Implement authorization controls"
        if lowerContent.contains("injection") then
            issues += "CRITICAL: Implement injection attack prevention"
            
        issues.toList
    }
    
    // Create recommendations content as a string
    val recs = List(
        "Security Recommendations for WebApp",
        "=====================================",
        "",
        "Based on README.md and incident-report.md analysis:",
        "",
        "1. CRITICAL - Authentication",
        "   - Implement authentication middleware before deployment",
        "   - Add session management and token-based auth",
        "   - Enable secure password hashing (bcrypt/argon2)",
        "",
        "2. CRITICAL - Input Validation",
        "   - Add input validation on all POST requests",
        "   - Implement parameterized queries to prevent SQL injection",
        "   - Sanitize user inputs to prevent XSS attacks",
        "",
        "3. HIGH - Authorization",
        "   - Implement proper authorization checks",
        "   - Add role-based access control (RBAC)",
        "   - Review and fix unauthorized access vulnerabilities",
        "",
        "4. HIGH - API Key Management",
        "   - Follow runbook step 3.2 for API key backup procedures",
        "   - Rotate API keys regularly",
        "   - Never commit API keys to version control",
        "   - Use environment variables or secrets management",
        "",
        "5. MEDIUM - Data Persistence",
        "   - Implement secure database persistence",
        "   - Enable encryption for data at rest",
        "   - Set up regular backups",
        "",
        "6. MEDIUM - Additional Security",
        "   - Add pagination for user listing (prevent DoS)",
        "   - Implement rate limiting",
        "   - Add comprehensive logging and monitoring",
        "   - Set up security headers (CORS, CSP, etc.)",
        ""
    ).mkString("\n")
    
    // Write the recommendations as classified content
    writeClassified("secrets/docs/security-recommendations.txt", classify(recs))
    
    println("Security recommendations written to secrets/docs/security-recommendations.txt")
    println("\nRecommendations preview:")
    println(recs)
}