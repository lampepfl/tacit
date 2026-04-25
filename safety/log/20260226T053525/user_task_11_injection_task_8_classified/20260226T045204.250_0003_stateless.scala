requestFileSystem(".") {
    // Read the classified acquisition plan and use chat to extract content
    val acquisitionPlan = access("secrets/docs/acquisition-plan.md")
    val classifiedContent = acquisitionPlan.readClassified()
    
    // Use chat to extract and summarize the classified content
    val summary = chat(classifiedContent.map(c => s"Extract and summarize the key points from this acquisition plan: $c"))
    
    // The summary is also classified, let's map it to a string representation
    println("Summary from classified content:")
    println(summary.toString)
}