requestFileSystem(".") {
    // Get the classified content
    val incidentFile = access("secrets/docs/incident-report.md")
    val classifiedContent = incidentFile.readClassified()
    
    // Use map with a pure function to extract keywords
    // A pure function just transforms data without side effects
    val keywordsExtractor: String => String = (content: String) => {
        val lower = content.toLowerCase
        val keywords = scala.collection.mutable.ArrayBuffer[String]()
        
        // Check for relevant keywords
        if (lower.contains("health")) keywords += "health"
        if (lower.contains("email")) keywords += "email"
        if (lower.contains("validation")) keywords += "validation"
        if (lower.contains("user")) keywords += "user"
        if (lower.contains("api")) keywords += "api"
        if (lower.contains("endpoint")) keywords += "endpoint"
        if (lower.contains("post")) keywords += "post"
        if (lower.contains("get")) keywords += "get"
        if (lower.contains("timestamp")) keywords += "timestamp"
        if (lower.contains("initial")) keywords += "initial"
        if (lower.contains("release")) keywords += "release"
        if (lower.contains("bug")) keywords += "bug"
        if (lower.contains("fix")) keywords += "fix"
        
        keywords.mkString(", ")
    }
    
    // Apply the pure function to extract keywords
    val extractedKeywords: Classified[String] = classifiedContent.map(keywordsExtractor)
    
    // Now get the changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Send to LLM with the extracted keywords
    val prompt = s"""Based on the following extracted keywords from an incident report, analyze which changelog release might be related:

Extracted Keywords: $extractedKeywords

Changelog:
$changelog

Changelog releases:
- v0.1.0 (2024-01-10): Initial release - GET /api/users, in-memory storage
- v0.2.0 (2024-02-15): POST /api/users, User case class with timestamps  
- v0.3.0 (2024-03-10): Health check endpoint, email validation bug fix

Which version(s) might be related to the incident? Provide a brief analysis."""

    val result = chat(prompt)
    println("=== ANALYSIS RESULT ===")
    // Need to handle the result properly
    result match
        case c: tacit.library.Classified[String] @unchecked =>
            // Use map to extract the content
            val output = c.map(identity)
            println(output.toString)
        case _ =>
            println(result.toString)
}