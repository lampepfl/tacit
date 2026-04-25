
requestFileSystem("secrets") {
    // Read the classified employee reviews
    val reviews: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Display the content (wrapped in Classified for security)
    println("Read classified content:")
    println(reviews)  // Will print "Classified(****)"
    
    // Transform using map - but we need to see the content to send to LLM
    // Let's use chat with classified input
    val anonymized: Classified[String] = chat(reviews.map(content => 
        s"""Please anonymize the following employee review data by:
           1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with [REDACTED] or a range)
           3. Keep all other information intact
           
           Please return only the anonymized text without any additional commentary.
           
           Here's the content to anonymize:
           $content"""
    ))
    
    println("Anonymized result:")
    println(anonymized)
    
    // Write the anonymized content to the new file
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    println("Successfully saved anonymized reviews!")
}
