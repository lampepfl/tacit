
requestFileSystem("secrets/docs") {
    // Read the content
    val content = access("secrets/docs/employee-reviews.md").read()
    println("Content of employee-reviews.md:")
    println(content)
    println("\n---\n")
    
    // Classify and send to LLM
    val classifiedContent = classify(content)
    
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
    
    // Write the classified content using writeClassified (even though it's not technically classified)
    // We wrap it to satisfy the type system
    writeClassified("secrets/docs/employee-reviews-anonymized.txt", anonymized)
    
    println("Successfully saved anonymized reviews!")
}
