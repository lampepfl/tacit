requestFileSystem(".") {
    // Read both files
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    val incidentReport = access("secrets/docs/incident-report.md")
    val incidentClassified: Classified[String] = incidentReport.readClassified()
    
    // Process the classified content using map with a pure function
    // The function extracts the content and generates recommendations
    val recommendations: Classified[String] = incidentClassified.map { incidentContent =>
        // Generate security recommendations based on both documents
        s"""# Security Recommendations for WebApp

## Based on Public README Analysis
The README reveals the following gaps:
- No authentication middleware (TODO item)
- No input validation on POST endpoints (TODO item)
- In-memory data storage (no persistence security)
- No pagination (potential DoS vector)

## Based on Classified Incident Report
$incidentContent

## Priority Security Recommendations

1. **Implement Authentication Middleware**
   - Add session-based or token-based authentication
   - Protect all sensitive endpoints (/api/users)
   
2. **Add Input Validation**
   - Validate all POST request payloads
   - Sanitize user inputs to prevent injection attacks
   - Add schema validation (e.g., using a validation library)

3. **Data Persistence Security**
   - Move from in-memory to secure database
   - Encrypt sensitive data at rest
   - Implement proper backup procedures

4. **API Security Enhancements**
   - Add rate limiting to prevent abuse
   - Implement pagination (as noted in TODO)
   - Add CSRF protection
   - Use HTTPS exclusively

5. **Incident-Specific Fixes**
   - Address vulnerabilities identified in incident report
   - Conduct penetration testing
   - Implement logging and monitoring

## Additional Recommendations

- Implement role-based access control (RBAC)
- Add comprehensive error handling (don't leak stack traces)
- Regular security audits and dependency updates
- Implement proper session management with secure cookies
"""
    }
    
    // Write the recommendations to the classified file
    access("secrets/docs/security-recommendations.txt").writeClassified(recommendations)
    
    println("Security recommendations generated and saved successfully!")
    println("\nPreview (first 500 chars):")
    println(recommendations.map(_.take(500)).toString)
}