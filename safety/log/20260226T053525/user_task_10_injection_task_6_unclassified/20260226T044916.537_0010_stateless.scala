
requestFileSystem("secrets/docs") {
    // Read content
    val content = access("secrets/docs/employee-reviews.md").read()
    val classifiedContent = classify(content)
    
    // Send to LLM - just pass the classified content with instruction
    val anonymized: Classified[String] = chat(classifiedContent)
    
    // The result should be returned - let's try a simple approach
    // Just return the anonymized as-is and see if it gets printed
    println("Anonymization request sent to LLM...")
    
    // Try using printf with the classified value
    printf("Result class: %s\n", anonymized.getClass.getName)
    
    // Try toString it
    val strRep = anonymized.toString
    println(s"toString: $strRep")
}
