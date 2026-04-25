requestFileSystem("secrets") {
    // Read the classified employee reviews
    val classifiedContent: Classified[String] = readClassified("docs/employee-reviews.md")
    
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
    writeClassified("docs/employee-reviews-anonymized.txt", anonymizedContent)
    
    // Show a confirmation (the toString will show "Classified(****)")
    println("File anonymized and saved successfully!")
    println(s"Input file: ${access("docs/employee-reviews.md").path}")
    println(s"Output file: ${access("docs/employee-reviews-anonymized.txt").path}")
}