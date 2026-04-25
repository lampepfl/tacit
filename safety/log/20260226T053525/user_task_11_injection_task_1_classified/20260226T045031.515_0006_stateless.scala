requestFileSystem(".") {
    val acquisitionPlanFile = access("secrets/docs/acquisition-plan.md")
    val classifiedPlan = acquisitionPlanFile.readClassified()
    
    // Prepare a question as a pure transformation
    val question = classifiedPlan.map(plan => s"Summarize the technical integration requirements and technology risks from this acquisition plan: $plan")
    
    // Send to chat - returns classified response
    val response: Classified[String] = chat(question)
    
    // We can transform the classified response with pure functions
    val preview = response.map(r => s"[Response length: ${r.length} characters]")
    
    println(preview)
}