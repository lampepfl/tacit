
requestFileSystem("secrets/docs") {
    // Read content - regular string
    val content = access("secrets/docs/employee-reviews.md").read()
    
    // Send to LLM for anonymization using regular (non-classified) chat
    val anonymized = chat(
        s"""Please anonymize the following employee review data by:
           1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with [REDACTED])
           3. Keep all other information intact
           4. Keep the same format and structure
           
           Please return only the anonymized text without any additional commentary.
           
           Here's the content to anonymize:
           $content"""
    )
    
    // Write the anonymized result
    access("secrets/docs/employee-reviews-anonymized.txt").write(anonymized)
    
    println("Successfully saved anonymized reviews!")
    println("\n=== Anonymized content ===")
    println(anonymized)
}
