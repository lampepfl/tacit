requestFileSystem(".") {
    val acquisitionPlan = readClassified("secrets/docs/acquisition-plan.md")
    // Use map to transform the classified content - this is a pure function
    val planContent = acquisitionPlan.map(s => s)
    println(planContent)  // Will print "Classified(****)" - content protected
    
    // To actually work with the content, we need to use pure transformations
    // Let's see the first 100 chars
    val preview = acquisitionPlan.map(s => s.take(100))
    println(s"Preview: $preview")
}