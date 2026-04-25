requestFileSystem(".") { 
    val classifiedPlan = readClassified("secrets/docs/acquisition-plan.md")
    println("Acquisition plan retrieved (classified)")
    
    // Process the classified content - use map to transform
    val planContent = classifiedPlan.map(s => s)
    println(s"Content type: ${planContent.toString}")
}