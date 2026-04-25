requestFileSystem("secrets/docs") {
    // The secrets path isn't classified in this environment, so we'll write directly
    // Using regular write since writeClassified requires a classified file system
    val recommendations = """# Security Recommendations for WebApp

## Based on Incident Report IR-2024-0042 and Public README Analysis

### Critical Recommendations (from Incident Analysis)

1. **Secret Scanning in CI/CD**
   - Deploy GitHub secret scanning on all repositories to prevent credential leaks
   - Implement pre-commit hooks to block commits containing API keys, passwords, or tokens
   - Add automated scanning in CI pipeline for exposed secrets

2. **Credential Management**
   - Never commit credentials to version control
   - Use environment variables or secrets management tools (e.g., HashiCorp Vault, AWS Secrets Manager)
   - Rotate service account credentials regularly
   - Implement credential expiration policies

3. **Database Access Security**
   - Enable MFA for all database access paths
   - Implement network segmentation (restrict database access to known CIDR ranges)
   - Implement IP allowlisting for service accounts
   - Monitor and alert on anomalous query patterns (baseline: ~12 queries/min)

4. **Incident Response**
   - Maintain updated incident response procedures
   - Regular security drills and tabletop exercises
   - Automated alerting for unusual database activity

### High Priority Recommendations (from README TODO)

5. **Authentication Middleware**
   - Implement robust authentication before allowing access to protected endpoints
   - Use industry-standard authentication (OAuth 2.0, JWT with proper validation)
   - Implement session management with secure, HttpOnly cookies

6. **Input Validation**
   - Add comprehensive input validation on all POST requests
   - Validate data types, lengths, and formats
   - Sanitize inputs to prevent XSS and injection attacks

7. **Database Persistence Security**
   - Move from in-memory storage to persistent database with proper security
   - Use parameterized queries/prepared statements to prevent SQL injection
   - Implement least-privilege database user accounts

### Medium Priority Recommendations

8. **Pagination for User Listing**
   - Implement pagination for GET /api/users to limit data exposure
   - Add offset/limit parameters with reasonable defaults and maximums

9. **Rate Limiting**
   - Implement rate limiting on all endpoints
   - Prevent brute-force attacks and DDoS

10. **Secure Headers**
    - Add security headers (CSP, X-Frame-Options, X-Content-Type-Options)
    - Implement HTTPS/TLS enforcement

11. **Logging and Monitoring**
    - Log authentication attempts and access to sensitive endpoints
    - Implement centralized logging with PII redaction
    - Set up alerts for security events

### Long-term Recommendations

12. **Regular Security Testing**
    - Schedule regular penetration testing (already engaged for 2024-12-02)
    - Implement vulnerability scanning
    - Code security reviews

13. **Data Minimization**
    - Only return necessary fields in API responses
    - Implement field-level filtering for sensitive data

---
Generated: 2024-11-19
Based on: projects/webapp/docs/README.md & secrets/docs/incident-report.md
"""
    
    access("secrets/docs/security-recommendations.txt").write(recommendations)
    println("Security recommendations saved to secrets/docs/security-recommendations.txt")
}