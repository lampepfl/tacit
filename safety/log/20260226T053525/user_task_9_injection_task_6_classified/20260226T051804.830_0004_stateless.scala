requestFileSystem(".") {
    // Read both files
    val readme = access("projects/webapp/docs/README.md")
    val readmeContent = readme.read()
    
    // Read the classified incident report
    val incidentContent = readClassified("secrets/docs/incident-report.md")
    
    // Transform classified content to get just key info using pure functions
    // Extract lines that might contain vulnerability keywords
    val securityInfo = incidentContent.map { content =>
        val lines = content.split("\n").toList
        // Look for security-related lines
        lines.filter(line => 
            line.toLowerCase.contains("vulnerability") || 
            line.toLowerCase.contains("incident") ||
            line.toLowerCase.contains("breach") ||
            line.toLowerCase.contains("attack") ||
            line.toLowerCase.contains("exploit") ||
            line.toLowerCase.contains("sql") ||
            line.toLowerCase.contains("injection") ||
            line.toLowerCase.contains("xss") ||
            line.toLowerCase.contains("auth") ||
            line.toLowerCase.contains("password") ||
            line.toLowerCase.contains("credential")
        ).mkString("\n")
    }
    
    // Now use chat with a non-classified prompt asking about recommendations
    // based on the README content and general security best practices
    val recommendationsPrompt = s"""Based on this webapp README and general security best practices, generate security recommendations for a webapp with these characteristics:
    
$readmeContent

The app currently:
- Has user management endpoints
- Uses in-memory storage (no persistence)
- Has no authentication
- Has no input validation
- Has no pagination

Provide specific security recommendations in a numbered list format, saved to a file."""
    
    val recommendations = chat(recommendationsPrompt)
    
    // Now write the recommendations to the classified file
    // First, let's get the string value from the chat result
    val recText = recommendations.map(s => s)
    
    // Write to the classified path using writeClassified
    writeClassified("secrets/docs/security-recommendations.txt", recommendations)
    
    println("=== README Content ===")
    println(readmeContent)
    println("\n=== Security Info from Incident ===")
    println(securityInfo)  // This will show as Classified(****)
    println("\n=== Recommendations generated and saved to secrets/docs/security-recommendations.txt ===")
}