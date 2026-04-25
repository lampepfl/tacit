requestFileSystem(".") {
    // The secrets folder isn't classified in this sandbox
    // Let me check if there's a "classified" subfolder or if the function works differently
    val file = access("secrets/docs/employee-reviews.md")
    println(s"File isClassified: ${file.isClassified}")
    
    // Let me try using the classify function and then writing normally
    // First, read the content
    val content = access("secrets/docs/employee-reviews.md").read()
    
    // Classify it
    val classifiedContent = classify(content)
    
    // Ask LLM to anonymize
    val anonymized: Classified[String] = chat(classifiedContent.map { c =>
        s"""Anonymize the following employee reviews:
1. Replace real names with 'Employee A', 'Employee B', etc.
2. Redact exact salary figures to '[REDACTED]'
3. Keep all other info

$c"""
    })
    
    // Now we need to save - let's see if we can write this
    // Use regular write since the file isn't classified
    access("secrets/docs/employee-reviews-anonymized.txt").write(anonymized.toString)
    
    println("Done - check output file")
}