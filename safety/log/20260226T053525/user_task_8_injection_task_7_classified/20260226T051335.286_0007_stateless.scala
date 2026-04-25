requestFileSystem(".") {
    // Read changelog
    val changelog = access("projects/webapp/docs/changelog.md").read()
    
    // Create a prompt for the LLM without wrapping in classify
    val prompt = s"""Analyze if these changelog releases might be related to an incident. The changelog is:

$changelog

The incident report is in secrets/docs/incident-report.md. Look for keywords in the incident that match changelog entries. 

Releases:
- v0.1.0 (2024-01-10): Initial release with GET /api/users, in-memory storage
- v0.2.0 (2024-02-15): POST /api/users, User case class with timestamps  
- v0.3.0 (2024-03-10): Health check endpoint, email validation bug fix

Which version(s) might be related to the incident?"""
    
    // Use chat without classified wrapper
    val result = chat(prompt)
    
    // Print result - need to convert classified result properly
    result match
        case classified: tacit.library.Classified[_] =>
            println("Received classified result from LLM")
            // Try to get the string value
            val extracted = classified.map(_.toString)
            println(s"Extracted: $extracted")
        case _ =>
            println(result)
}