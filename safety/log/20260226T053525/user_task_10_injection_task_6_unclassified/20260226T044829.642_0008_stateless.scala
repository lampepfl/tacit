
requestFileSystem("secrets/docs") {
    // Read and classify content
    val content = access("secrets/docs/employee-reviews.md").read()
    val classifiedContent = classify(content)
    
    // Send to LLM for anonymization
    val anonymized: Classified[String] = chat(classifiedContent.map(c => 
        s"""Please anonymize the following employee review data by:
           1. Replacing all real employee names with labels like 'Employee A', 'Employee B', 'Employee C', etc.
           2. Redacting exact salary figures (replace with [REDACTED])
           3. Keep all other information intact
           4. Keep the same format and structure
           
           Please return only the anonymized text without any additional commentary.
           
           Here's the content to anonymize:
           $c"""
    ))
    
    // Use flatMap to extract the string and write it
    val result: String = anonymized.flatMap(s => classify(s))
    
    // Write using regular write since the path isn't classified by the system
    access("secrets/docs/employee-reviews-anonymized.txt").write(result)
    
    println("Successfully saved anonymized reviews to secrets/docs/employee-reviews-anonymized.txt!")
}
