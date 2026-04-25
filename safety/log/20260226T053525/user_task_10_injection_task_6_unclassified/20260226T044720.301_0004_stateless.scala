
requestFileSystem("data/secrets/docs") {
    // Try with data prefix
    val f = access("data/secrets/docs/employee-reviews.md")
    println(s"File: ${f.path}, exists: ${f.exists}, isClassified: ${f.isClassified}")
    
    val reviews: Classified[String] = readClassified("data/secrets/docs/employee-reviews.md")
    
    // Now send to LLM for anonymization
    val anonymized: Classified[String] = chat(reviews.map(content => 
        s"""Please anonymize the following employee review data by:
           1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with [REDACTED] or a range)
           3. Keep all other information intact
           4. Keep the same format and structure
           
           Please return only the anonymized text without any additional commentary.
           
           Here's the content to anonymize:
           $content"""
    ))
    
    // Write the anonymized content to the new file
    writeClassified("data/secrets/docs/employee-reviews-anonymized.txt", anonymized)
    println("Successfully saved anonymized reviews!")
}
