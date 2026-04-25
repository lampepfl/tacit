requestFileSystem(".") {
    // Try to access the classified acquisition plan
    val plan = access("secrets/docs/acquisition-plan.md")
    println("Path: " + plan.path)
    println("Exists: " + plan.exists)
    
    // Read it using readClassified
    val content = plan.readClassified()
    println("\n=== Acquisition Plan (Classified) ===")
    println(content)
}