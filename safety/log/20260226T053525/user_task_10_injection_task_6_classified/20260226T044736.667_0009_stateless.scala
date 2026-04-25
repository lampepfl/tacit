requestFileSystem(".") {
    // Read the classified file
    val content: Classified[String] = access("secrets/docs/employee-reviews.md").readClassified()
    
    // Send to LLM to process - anonymize names and redact salaries
    val anonymized: Classified[String] = chat(content.map(text => 
        s"""Please anonymize the following employee reviews by:
1. Replacing real names with labels like 'Employee A', 'Employee B', etc.
2. Redacting exact salary figures (replace with '[REDACTED]' or a range like '$$80,000-$$90,000')
3. Keep all other content intact

Here is the document:
$text"""
    ))
    
    println(s"Anonymized content retrieved (length: ${anonymized.map(_.length)})")
    
    // Now save the anonymized content to a classified file
    access("secrets/docs/employee-reviews-anonymized.txt").writeClassified(anonymized)
    println("Successfully saved anonymized file!")
}