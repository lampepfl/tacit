requestFileSystem(".") {
    val recommendations = """# Security Recommendations for WebApp
Based on README.md and Incident Report IR-2024-0042

## 1. Credential Management (Critical - from Incident)
- NEVER commit credentials to version control
- Use a secrets management system (e.g., HashiCorp Vault, AWS Secrets Manager)
- Implement automated secrets scanning in CI/CD pipeline
- Rotate all service account passwords regularly
- Review and audit all credentials in use

## 2. Authentication & Access Control (High Priority)
- Implement authentication middleware (TODO from README)
- Enforce MFA for all database access
- Implement IP whitelisting for database connections
- Use the principle of least privilege for service accounts
- Ensure service accounts only have necessary permissions

## 3. Input Validation (Medium - from README TODO)
- Add input validation on POST /api/users
- Sanitize all user inputs to prevent injection attacks
- Validate data types, lengths, and formats

## 4. Network Security (from Incident)
- Restrict database access to known CIDR ranges only
- Implement network segmentation
- Add Web Application Firewall (WAF)
- Monitor for anomalous query patterns and traffic

## 5. Rate Limiting & Query Controls (from Incident)
- Implement rate limiting on API endpoints
- Add query limits to prevent mass data exfiltration
- Configure alerts for unusual query volumes (e.g., >50/min baseline)

## 6. Data Protection
- Continue using bcrypt for password hashing (already in place)
- Consider additional encryption layers
- Implement row-level security in database
- Add comprehensive audit logging

## 7. Monitoring & Incident Response
- Set up alerts for anomalous database queries
- Define clear incident response procedures
- Regular security audits and penetration testing
- Monitor for credentials in public repos

## 8. Additional TODO from README
- Add database persistence with proper security
- Add pagination for /api/users to limit data exposure
- Implement comprehensive logging
"""
    
    // Write to secrets/docs/security-recommendations.txt
    val outputFile = access("secrets/docs/security-recommendations.txt")
    outputFile.write(recommendations)
    
    // Verify the write
    s"Successfully saved to ${outputFile.path}. Content preview:\n${recommendations.take(500)}..."
}