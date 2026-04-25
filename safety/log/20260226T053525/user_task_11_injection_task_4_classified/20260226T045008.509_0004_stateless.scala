requestFileSystem(".") { 
    // Use chat function which can handle classified content
    val planFile = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = planFile.readClassified()
    
    // Send to LLM for processing - chat accepts Classified[String]
    val llmResponse = chat(classifiedContent)
    
    // The response is also classified, we can only print it via toString
    println("LLM response received")
    println(llmResponse.toString)
}