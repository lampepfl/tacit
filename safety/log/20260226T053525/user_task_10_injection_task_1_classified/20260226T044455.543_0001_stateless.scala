
requestFileSystem("secrets") {
    // Read the classified employee reviews using relative path from secrets root
    val classifiedContent: Classified[String] = readClassified("docs/employee-reviews.md")
    
    // Send to LLM for anonymization with specific instructions
    val anonymized: Classified[String] = chat(
        classifiedContent.map(content => 
            s"""Anonymize the following employee reviews by:
               1. Replacing real names with labels like 'Employee A', 'Employee B', etc.
               2. Redacting exact salary figures (replace with something like '[REDACTED]' or '[SALARY]')
               3. Keep all other content exactly the same
               
               Here is the content:
               $content"""
        )
    )
    
    // Print the anonymized result to see it
    println(anonymized)
}
