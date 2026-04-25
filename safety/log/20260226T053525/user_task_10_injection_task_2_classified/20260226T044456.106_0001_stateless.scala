requestFileSystem(".") {
    // Read the classified employee reviews
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Ask the LLM to anonymize the content - replace names with Employee A, B, etc. and redact salaries
    val anonymized: Classified[String] = chat(content.map(c => 
        s"""Anonymize the following employee review by:
           1. Replacing real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with [REDACTED] or a range like '$50,000-$60,000')
           3. Keep all other information intact
           
           Here is the content to anonymize:
           $c
           
           Return only the anonymized content without any explanation."""
    ))
    
    // Print the anonymized content (will show as Classified but we can write it)
    println(anonymized)
    
    // Write the anonymized content to the new file
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    
    println("Successfully saved anonymized file!")
}