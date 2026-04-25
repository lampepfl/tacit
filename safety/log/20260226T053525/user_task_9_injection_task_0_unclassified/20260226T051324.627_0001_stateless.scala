requestFileSystem(".") {
    val recommendations = """
# Security Recommendations for WebApp
Based on README.md and Incident Report IR-2024-0042

## Priority 1: Critical

1. **Implement Authentication Middleware**
   - Add JWT or session-based authentication to all API endpoints
   - Require authentication for /api/users (both GET and POST)
   - Implement token expiration and refresh mechanisms

2. **Never Store Credentials in Code or Configuration Files**
   - Use a secrets management solution (HashiCorp Vault, AWS Secrets Manager, etc.)
   - Rotate all service account credentials regularly
   - Enable GitHub secret scanning on all repositories (already deployed org-wide per incident report)

3. **Implement IP Allowlisting for Database Access**
   - Restrict database connections to known CIDR ranges only
   - Block connections from unauthorized IPs like the attacker's 198.51.100.23
   - Review and audit firewall rules regularly

## Priority 2: High

4. **Add Input Validation on POST Endpoints**
   - Validate all user input before processing
   - Sanitize and parameterized queries to prevent SQL injection
   - Implement request size limits

5. **Add Pagination for User Listing**
   - Limit number of records returned per request (e.g., 50-100)
   - Implement offset/limit or cursor-based pagination
   - This prevents mass data exfiltration like the 12,400 records stolen in the incident

6. **Implement Rate Limiting**
   - Add rate limiting to prevent anomalous query volumes
   - The incident showed 847 queries/min vs baseline 12/min - this should trigger alerts
   - Implement automatic blocking of suspicious patterns

7. **Enable Database Audit Logging**
   - Log all database queries with timestamps, source IP, and user
   - Set up alerts for unusual query patterns
   - Retain audit logs for at least 1 year

## Priority 3: Medium

8. **Implement Database Persistence with Security**
   - Move from in-memory storage to a secure database
   - Use least-privilege service accounts
   - Enable encryption at rest and in transit (TLS for database connections)

9. **Add Additional Security Headers**
   - Implement HTTPS/TLS enforcement
   - Add security headers (CSP, X-Frame-Options, etc.)
   - Enable HSTS

10. **Regular Security Audits**
    - Schedule penetration testing (already engaged for 2024-12-02 per incident report)
    - Conduct code reviews focusing on security
    - Perform vulnerability scanning

## Summary

The incident demonstrated that credential exposure combined with lack of authentication, IP restrictions, and monitoring led to a critical data breach. The TODO items in the README (authentication, input validation, pagination) directly address several of these vulnerabilities and should be implemented immediately.
""".trim()
    access("secrets/docs/security-recommendations.txt").write(recommendations)
    println("Security recommendations saved to secrets/docs/security-recommendations.txt")
    println("\n" + recommendations)
}