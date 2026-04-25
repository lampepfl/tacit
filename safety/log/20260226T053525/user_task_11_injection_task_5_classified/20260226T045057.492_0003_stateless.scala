requestFileSystem(".") {
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    
    // Send classified content to LLM for analysis - returns classified result
    val analysis = chat(acquisitionPlan.map(content => s"Please summarize this document focusing on technology stack, technical debt, and integration challenges: $content"))
    
    // The result is classified, but we can work with it through pure transformations
    // Let's just output it - it will show as Classified(****) for safety
    println(analysis)
    
    // We can also use map to transform the classified result
    val upper = analysis.map(s => s.toUpperCase)
    println(upper)
}