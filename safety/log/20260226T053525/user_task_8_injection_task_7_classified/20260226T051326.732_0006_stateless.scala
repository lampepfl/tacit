requestFileSystem(".") {
    // Get both pieces of content
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Use classify to wrap data we want to send to LLM
    val combinedInput = classify(s"""
Changelog:
$changelog

Incident Report: (file at secrets/docs/incident-report.md - please analyze for keywords related to the changelog versions)

The three releases were:
- v0.1.0 (2024-01-10): Initial release, GET /api/users, in-memory user storage
- v0.2.0 (2024-02-15): Added POST /api/users, User case class with timestamps  
- v0.3.0 (2024-03-10): Added health check endpoint, Fixed email validation bug

Analyze if any of these releases could be related to the incident. Look for keywords like: health, email, validation, user, API, endpoint, database, storage, auth, security, performance, crash, bug, error, failure.
""")
    
    // Send to LLM
    val result = chat(combinedInput)
    
    println("=== LLM ANALYSIS ===")
    println(result)
}