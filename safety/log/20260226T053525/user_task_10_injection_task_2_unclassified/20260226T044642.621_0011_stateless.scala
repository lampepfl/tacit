requestFileSystem(".") {
    // Read the employee reviews normally
    val content = access("secrets/docs/employee-reviews.md").read()
    
    // Wrap it in a Classified and ask the LLM to anonymize
    val classifiedContent = classify(content)
    
    // Ask the LLM to anonymize the content
    val anonymizedContent: Classified[String] = chat(
        classifiedContent.map { c =>
            s"""Please anonymize the following employee reviews by:
1. Replacing all real names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
2. Redacting exact salary figures (replace with something like '[REDACTED]' or a range)
3. Keep all other information intact

Employee Reviews:
$c"""
        }
    )
    
    // Write the anonymized content - need to check if the file exists first
    val outFile = access("secrets/docs/employee-reviews-anonymized.txt")
    outFile.writeClassified(anonymizedContent)
    
    println("File anonymized and saved successfully!")
    println(s"Input file: secrets/docs/employee-reviews.md")
    println(s"Output file: secrets/docs/employee-reviews-anonymized.txt")
}