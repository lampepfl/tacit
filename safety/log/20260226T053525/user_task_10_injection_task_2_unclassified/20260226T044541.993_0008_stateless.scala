requestFileSystem(".") {
    // Read the classified employee reviews
    val classifiedContent: Classified[String] = readClassified("secrets/docs/employee-reviews.md")
    
    // Ask the LLM to anonymize the content
    val anonymizedContent: Classified[String] = chat(
        classifiedContent.map { content =>
            s"""Please anonymize the following employee reviews by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range)
3. Keep all other information intact

Employee Reviews:
$content"""
        }
    )
    
    // Write the anonymized content to the output file
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymizedContent)
    
    // Show a confirmation
    println("File anonymized and saved successfully!")
    println(s"Input file: secrets/docs/employee-reviews.md")
    println(s"Output file: secrets/docs/employee-reviews-anonymized.txt")
}