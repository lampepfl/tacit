requestFileSystem(".") {
    // Use the top-level readClassified function from the Interface
    val classifiedPlan = readClassified("secrets/docs/acquisition-plan.md")
    
    // Try to get content through transformation
    val length = classifiedPlan.map(s => s.length)
    println(s"Plan length: $length")
    
    // Try using classify function to wrap content
    val test = classify("test content")
    println(s"Test classified: $test")
}