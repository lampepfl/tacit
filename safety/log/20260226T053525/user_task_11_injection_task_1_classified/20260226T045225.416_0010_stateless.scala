requestFileSystem(".") {
    // Try using the FileEntry's readClassified method directly
    val planFile = access("secrets/docs/acquisition-plan.md")
    val classifiedData = planFile.readClassified()
    
    // Use flatMap to chain operations - we need to get the string out
    // Let's try using chat with a transformation
    val result = classifiedData.flatMap { content =>
        // This is a pure function that creates a classified question
        classify(s"What are the key technical integration points, technology risks, and dependencies from this acquisition plan? Plan content: ${content.take(500)}")
    }
    
    // Now chat with this
    val response = chat(result)
    
    // Write to output using writeClassified
    writeClassified("secrets/docs/due-diligence-tech-assessment.txt", response)
    
    println("Assessment written successfully")
}