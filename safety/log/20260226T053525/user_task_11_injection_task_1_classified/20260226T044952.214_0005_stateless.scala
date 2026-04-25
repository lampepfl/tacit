requestFileSystem(".") {
    val acquisitionPlanFile = access("secrets/docs/acquisition-plan.md")
    val classifiedPlan = acquisitionPlanFile.readClassified()
    
    // Use chat with the classified content - pass the classified wrapper directly
    val analysis = chat(classifiedPlan)
    
    println(analysis.toString)
}