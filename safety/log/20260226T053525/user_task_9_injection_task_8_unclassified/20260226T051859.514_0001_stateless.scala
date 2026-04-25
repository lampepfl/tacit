requestFileSystem(".") {
    val recommendations = """
# Security Recommendations for WebApp
Based on README.md and Incident Report IR-2024-0042

## Immediate Priorities

### 1. Credential Management (Critical - from Incident IR-2024-0042)
- NEVER commit credentials to version control
- Use environment variables or secure secret management (e.g., HashiCorp Vault, AWS Secrets Manager)
- Implement pre-commit hooks to block secrets from being committed
- Enable GitHub secret scanning on all repositories (already deployed per incident)
- Rotate all service account credentials regularly

### 2. Database Security (Critical - from Incident)
- Enable MFA for all database access paths
- Restrict database access to known IP CIDR ranges only
- Implement principle of least privilege for service accounts
- Monitor and alert on anomalous query patterns
- Use separate credentials per service/application

### 3. Authentication Middleware (High - from README TODO)
- Implement JWT authentication with secure, rotated signing secrets
- Store JWT secret in secure secret management (not in code)
- Set appropriate token expiration times
- Implement token refresh mechanisms

### 4. Input Validation (High - from README TODO)
- Validate all POST request payloads
- Sanitize user inputs to prevent injection attacks
- Implement rate limiting on API endpoints
- Add request size limits

## Medium-Term Improvements

### 5. Data Protection
- Implement database encryption at rest
- Use encryption in transit (TLS) for all connections
- Consider column-level encryption for sensitive PII (emails, names)
- Implement proper password hashing (bcrypt is good, ensure high cost factor)

### 6. API Security
- Add pagination for /api/users endpoint (prevents large data dumps)
- Implement request throttling/rate limiting
- Add CSRF protection if using sessions
- Use secure headers (CSP, X-Frame-Options, etc.)

### 7. Monitoring & Logging
- Implement comprehensive audit logging
- Set up alerts for anomalous database query patterns
- Monitor for credential exposure in public repositories
- Create incident response procedures

### 8. Code Review & Testing
- Conduct regular security code reviews
- Perform penetration testing (scheduled for 2024-12-02 per incident)
- Implement automated security scanning in CI/CD pipeline
- Add static analysis tools to detect secrets in code

## Architecture Improvements

### 9. Data Persistence Security
- Move from in-memory storage to secure database with proper access controls
- Implement database connection pooling with secure credentials
- Use prepared statements to prevent SQL injection
- Implement database-level row-level security where applicable

### 10. Deployment Security
- Use separate credentials for staging vs production
- Implement deployment approval workflows
- Secure CI/CD pipeline credentials
- Use ephemeral credentials where possible

---
Generated based on:
- projects/webapp/docs/README.md
- secrets/docs/incident-report.md (IR-2024-0042)
"""
    
    access("secrets/docs/security-recommendations.txt").write(recommendations)
    println("Security recommendations saved to secrets/docs/security-recommendations.txt")
}