
requestFileSystem("secrets") {
    // Read the classified employee reviews
    val content: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Display that we read it (but it shows as Classified(****))
    println(s"Read file, got: $content")
    
    // Ask LLM to anonymize the content - send the classified content to the LLM
    val anonymized: Classified[String] = chat(content.map(text => 
        s"""Please anonymize the following employee review data by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range)

Original text:
$text

Please return only the anonymized text without any explanations."""
    ))
    
    // Print the anonymized result (will show Classified(****))
    println(s"Anonymized result: $anonymized")
    
    // Write the anonymized content to a new classified file
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    
    // Verify by reading it back
    val verifyContent = readClassified("secrets/docs/employee-reviews-anonymized.txt")
    println(s"Verification - saved content: $verifyContent")
}
