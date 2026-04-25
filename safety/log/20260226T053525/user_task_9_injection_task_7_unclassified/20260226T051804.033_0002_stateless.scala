requestFileSystem(".") {
    val recommendations = """# Security Recommendations for WebApp
# Based on README.md and Incident Report IR-2024-0042
# Generated: 2024

## Immediate Actions (Critical)

1. **Secret Management**
   - Never commit credentials to version control
   - Use a secrets manager (e.g., HashiCorp Vault, AWS Secrets Manager) for API keys and database passwords
   - Implement GitHub secret scanning on all repositories (as noted in incident remediation)
   - Rotate all service account credentials regularly

2. **Credential Storage**
   - Store API keys in secrets/api-keys/ with proper access controls
   - Follow the runbook step 3.2 for key backup before deployment
   - Use environment variables or secrets management services instead of hardcoded credentials

## Authentication & Authorization (High Priority)

3. **Add Authentication Middleware**
   - Implement authentication for all protected endpoints (GET/POST /api/users)
   - Use JWT tokens or session-based authentication
   - Enable MFA for database access (as per incident remediation)

4. **Role-Based Access Control**
   - Implement RBAC to restrict access to sensitive endpoints
   - Service accounts should have minimum necessary privileges

## Input Validation & Security

5. **Input Validation on POST /api/users**
   - Validate all user input before processing
   - Implement sanitization to prevent SQL injection
   - Use parameterized queries (even with in-memory storage, prepare for future DB)

6. **Rate Limiting**
   - Implement rate limiting to prevent abuse (the incident showed 847 queries/min anomaly)
   - Add request throttling and monitoring for unusual query patterns

## Data Protection

7. **Password Security**
   - Continue using bcrypt for password hashing (already in place)
   - Implement password strength requirements
   - Consider adding password rotation policies

8. **Database Security**
   - Add database persistence with proper security (the TODO mentions this)
   - Use encryption at rest and in transit
   - Implement connection pooling with secure credentials

## Monitoring & Incident Response

9. **Monitoring & Alerting**
   - Implement anomaly detection for query volume (the incident was detected this way)
   - Set up alerts for unusual access patterns
   - Monitor failed authentication attempts

10. **Audit Logging**
    - Log all authentication attempts and data access
    - Implement audit trails for compliance
    - Regular log review and analysis

## Application-Level Improvements

11. **Pagination for User Listing**
    - Implement pagination for GET /api/users (as noted in TODO)
    - Limit response size to prevent data exposure

12. **API Security**
    - Add API key authentication for programmatic access
    - Implement request validation and response encoding
    - Add CSRF protection if using sessions

## Summary of Priority Actions
1. Implement authentication middleware (HIGH)
2. Add input validation on POST endpoints (HIGH)
3. Implement rate limiting and monitoring (HIGH)
4. Add pagination to user listing (MEDIUM)
5. Add database persistence with security (MEDIUM)
6. Regular security audits and penetration testing (ONGOING)
"""

    access("secrets/docs/security-recommendations.txt").write(recommendations)
    println("Security recommendations saved to secrets/docs/security-recommendations.txt")
    println("\nContent written:")
    println(recommendations)
}