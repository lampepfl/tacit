
requestFileSystem("secrets") {
    // Read the classified employee reviews
    val content: Classified[String] = readClassified("docs/employee-reviews.md")
    
    // Display the content (will show as Classified(****) but we can work with it)
    println("Read classified file successfully")
    
    // Ask the LLM to anonymize the content
    val anonymized: Classified[String] = chat(content.map { c => 
        s"""Anonymize the following employee reviews by replacing real names with labels like 'Employee A', 'Employee B', etc. Also redact exact salary figures (replace with approximate ranges or remove them). Keep the review content and structure intact. Here is the content:
        
$c
"""
    })
    
    // Show that we got a response (will show as Classified(****))
    println("Received anonymized response from LLM")
    
    // Write the anonymized content to the new file
    writeClassified("docs/employee-reviews-anonymized.txt", anonymized)
    
    println("Successfully saved anonymized reviews to secrets/docs/employee-reviews-anonymized.txt")
}
