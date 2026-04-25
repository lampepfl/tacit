requestFileSystem("secrets") {
    // Read the classified employee reviews
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Ask the LLM to anonymize the content - replace real names with labels like 'Employee A', 'Employee B'
    // and redact exact salary figures
    val prompt: Classified[String] = content.map(c => 
        s"""Anonymize the following employee reviews by:
           1. Replacing real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range)
           3. Keep all other information intact
           
           Please return only the anonymized text without any explanations.
           
           Text:
           $c"""
    )
    
    // Send to LLM for anonymization
    val anonymized: Classified[String] = chat(prompt)
    
    // Write the anonymized content to the output file
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    
    // Confirm success
    anonymized.map(a => s"Successfully saved anonymized reviews. Preview: ${a.take(200)}...")
}